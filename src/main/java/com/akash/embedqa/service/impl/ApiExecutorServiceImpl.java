package com.akash.embedqa.service.impl;

import com.akash.embedqa.constant.AppConstant;
import com.akash.embedqa.enums.AuthType;
import com.akash.embedqa.enums.BodyType;
import com.akash.embedqa.enums.HttpMethod;
import com.akash.embedqa.model.dtos.request.AuthConfigDTO;
import com.akash.embedqa.model.dtos.request.ExecuteRequestDTO;
import com.akash.embedqa.model.dtos.request.KeyValuePairDTO;
import com.akash.embedqa.model.dtos.response.ApiResponseDTO;
import com.akash.embedqa.model.entities.RequestHistory;
import com.akash.embedqa.service.ApiExecutorService;
import com.akash.embedqa.service.EnvironmentService;
import com.akash.embedqa.service.HistoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Author: akash
 * Date: 17/12/25
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApiExecutorServiceImpl implements ApiExecutorService {

    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final EnvironmentService environmentService;
    private final HistoryService historyService;

    // Pattern to match environment variables: {{variableName}}
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{([^}]+)}}");

    @Override
    public ApiResponseDTO executeRequest(ExecuteRequestDTO request) {
        return executeAndSave(request, true);
    }

    @Override
    public ApiResponseDTO executeAndSave(ExecuteRequestDTO request, boolean saveToHistory) {
        long startTime = System.currentTimeMillis();

        try {
            // Resolve environment variables if environment is specified
            Map<String, String> variables = getEnvironmentVariables(request.getEnvironmentId());

            // Build the URL with query parameters
            String resolvedUrl = resolveVariables(request.getUrl(), variables);
            URI uri = buildUri(resolvedUrl, request.getQueryParams(), variables);

            // Create the appropriate HTTP request
            HttpUriRequestBase httpRequest = request.getMethod().create(uri);

            // Add headers
            addHeaders(httpRequest, request.getHeaders(), variables);

            // Add authentication
            addAuthentication(httpRequest, request.getAuthType(), request.getAuthConfig(), variables);

            // Add body for methods that support it
            if (hasBody(request.getMethod().name()) && request.getBody() != null) {
                addBody(httpRequest, request.getBody(), request.getBodyType(), variables);
            }

            // Execute the request
            log.debug("Executing {} request to: {}", request.getMethod(), uri);

            ApiResponseDTO response = httpClient.execute(httpRequest, httpResponse -> {
                long responseTime = System.currentTimeMillis() - startTime;
                return buildResponse(httpResponse, responseTime, uri.toString(), request.getMethod().name());
            });

            if (saveToHistory) {
                saveToHistory(request, response, uri.toString());
            }

            return response;

        } catch (URISyntaxException e) {
            log.error("Invalid URL: {}", request.getUrl(), e);
            ApiResponseDTO errorResponse = buildErrorResponse(request, "Invalid URL: " + e.getMessage(),
                    System.currentTimeMillis() - startTime);
            if (saveToHistory) {
                saveToHistory(request, errorResponse, request.getUrl());
            }
            return errorResponse;
        } catch (IOException e) {
            log.error("Request execution failed", e);
            ApiResponseDTO errorResponse = buildErrorResponse(request, "Connection failed: " + e.getMessage(),
                    System.currentTimeMillis() - startTime);
            if (saveToHistory) {
                saveToHistory(request, errorResponse, request.getUrl());
            }
            return errorResponse;
        } catch (Exception e) {
            log.error("Unexpected error during request execution", e);
            ApiResponseDTO errorResponse = buildErrorResponse(request, "Unexpected error: " + e.getMessage(),
                    System.currentTimeMillis() - startTime);
            if (saveToHistory) {
                saveToHistory(request, errorResponse, request.getUrl());
            }
            return errorResponse;
        }
    }

    private Map<String, String> getEnvironmentVariables(Long environmentId) {
        if (environmentId == null) {
            return Collections.emptyMap();
        }
        return environmentService.getVariablesAsMap(environmentId);
    }

    private String resolveVariables(String input, Map<String, String> variables) {
        if (input == null || variables.isEmpty()) {
            return input;
        }

        Matcher matcher = VARIABLE_PATTERN.matcher(input);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String varName = matcher.group(1).trim();
            String replacement = variables.getOrDefault(varName, matcher.group(0));
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    private boolean hasBody(String method) {
        return Set.of(HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH).contains(method.toUpperCase());
    }

    private void addHeaders(HttpUriRequestBase request, List<KeyValuePairDTO> headers,
                            Map<String, String> variables) {
        if (headers == null) return;

        for (KeyValuePairDTO header : headers) {
            if (Boolean.TRUE.equals(header.getEnabled()) &&
                    header.getKey() != null && !header.getKey().isBlank()) {
                String name = resolveVariables(header.getKey(), variables);
                String value = resolveVariables(header.getValue(), variables);
                request.addHeader(name, value);
            }
        }
    }

    private void addAuthentication(HttpUriRequestBase request, AuthType authType,
                                   AuthConfigDTO authConfig, Map<String, String> variables) {
        if (authType == null || authConfig == null) return;

        authType.apply(request, authConfig, variables);
    }

    private URI buildUri(String baseUrl, List<KeyValuePairDTO> queryParams,
                         Map<String, String> variables) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(baseUrl);

        if (queryParams != null) {
            for (KeyValuePairDTO param : queryParams) {
                if (Boolean.TRUE.equals(param.getEnabled()) &&
                        param.getKey() != null && !param.getKey().isBlank()) {
                    String key = resolveVariables(param.getKey(), variables);
                    String value = resolveVariables(param.getValue(), variables);
                    uriBuilder.addParameter(key, value);
                }
            }
        }

        return uriBuilder.build();
    }

    private void saveToHistory(ExecuteRequestDTO request, ApiResponseDTO response, String resolvedUrl) {
        try {
            // Convert headers to JSON
            String requestHeadersJson = null;
            if (request.getHeaders() != null && !request.getHeaders().isEmpty()) {
                Map<String, String> headers = request.getHeaders().stream()
                        .filter(h -> Boolean.TRUE.equals(h.getEnabled()) && h.getKey() != null && !h.getKey().isBlank())
                        .collect(java.util.stream.Collectors.toMap(
                                KeyValuePairDTO::getKey,
                                h -> h.getValue() != null ? h.getValue() : "",
                                (v1, v2) -> v2
                        ));
                requestHeadersJson = objectMapper.writeValueAsString(headers);
            }

            // Convert query params to JSON
            String queryParamsJson = null;
            if (request.getQueryParams() != null && !request.getQueryParams().isEmpty()) {
                Map<String, String> params = request.getQueryParams().stream()
                        .filter(p -> Boolean.TRUE.equals(p.getEnabled()) && p.getKey() != null && !p.getKey().isBlank())
                        .collect(java.util.stream.Collectors.toMap(
                                KeyValuePairDTO::getKey,
                                p -> p.getValue() != null ? p.getValue() : "",
                                (v1, v2) -> v2
                        ));
                queryParamsJson = objectMapper.writeValueAsString(params);
            }

            // Convert auth config to JSON
            String authConfigJson = null;
            if (request.getAuthConfig() != null) {
                authConfigJson = objectMapper.writeValueAsString(request.getAuthConfig());
            }

            // Convert response headers to JSON
            String responseHeadersJson = null;
            if (response.getHeaders() != null && !response.getHeaders().isEmpty()) {
                Map<String, String> respHeaders = response.getHeaders().stream()
                        .collect(java.util.stream.Collectors.toMap(
                                ApiResponseDTO.HeaderDTO::getName,
                                h -> h.getValue() != null ? h.getValue() : "",
                                (v1, v2) -> v2
                        ));
                responseHeadersJson = objectMapper.writeValueAsString(respHeaders);
            }

            RequestHistory history = RequestHistory.builder()
                    .url(resolvedUrl)
                    .method(request.getMethod())
                    .requestHeaders(requestHeadersJson)
                    .queryParams(queryParamsJson)
                    .requestBody(request.getBody())
                    .bodyType(request.getBodyType() != null ? request.getBodyType().name() : null)
                    .authType(request.getAuthType() != null ? request.getAuthType().name() : null)
                    .authConfig(authConfigJson)
                    .statusCode(response.getStatusCode() != null ? response.getStatusCode() : 0)
                    .statusText(response.getStatusText())
                    .responseHeaders(responseHeadersJson)
                    .responseBody(response.getBody())
                    .responseTime(response.getResponseTimeMs() != null ? response.getResponseTimeMs() : 0L)
                    .responseSize(response.getBodySize())
                    .executedAt(java.time.LocalDateTime.now())
                    .build();

            historyService.saveHistory(history);
            log.debug("Saved request history for URL: {}", resolvedUrl);

        } catch (Exception e) {
            log.error("Failed to save request history: {}", e.getMessage(), e);
            // Don't throw - we don't want history saving failures to affect the response
        }
    }

    private void addBody(HttpUriRequestBase request, String body, BodyType bodyType,
                         Map<String, String> variables) {
        if (body == null || body.isBlank()) return;

        String resolvedBody = resolveVariables(body, variables);
        String contentType = bodyType.getContentType();

        StringEntity entity = new StringEntity(resolvedBody, ContentType.parse(contentType));

        if (request instanceof HttpPost post) {
            post.setEntity(entity);
        } else if (request instanceof HttpPut put) {
            put.setEntity(entity);
        } else if (request instanceof HttpPatch patch) {
            patch.setEntity(entity);
        }

        // Set content type header if not already set
        if (request.getFirstHeader(AppConstant.CONTENT_TYPE) == null) {
            request.addHeader(AppConstant.CONTENT_TYPE, contentType);
        }
    }

    private ApiResponseDTO buildResponse(ClassicHttpResponse response, long responseTime,
                                         String requestUrl, String requestMethod) throws IOException {
        // Get response body
        String body = null;
        Long bodySize = 0L;
        String contentType = null;

        HttpEntity entity = response.getEntity();
        if (entity != null) {
            byte[] bodyBytes = EntityUtils.toByteArray(entity);
            bodySize = (long) bodyBytes.length;
            body = new String(bodyBytes, StandardCharsets.UTF_8);

            Header contentTypeHeader = entity.getContentType() != null ?
                    new org.apache.hc.core5.http.message.BasicHeader(AppConstant.CONTENT_TYPE, entity.getContentType()) :
                    response.getFirstHeader(AppConstant.CONTENT_TYPE);
            if (contentTypeHeader != null) {
                contentType = contentTypeHeader.getValue();
            }
        }

        // Get response headers
        List<ApiResponseDTO.HeaderDTO> headers = Arrays.stream(response.getHeaders())
                .map(h -> ApiResponseDTO.HeaderDTO.builder()
                        .name(h.getName())
                        .value(h.getValue())
                        .build())
                .collect(Collectors.toList());

        // Get status
        int statusCode = response.getCode();
        String statusText = response.getReasonPhrase();

        // Pretty print JSON if content type is JSON
        if (contentType != null && contentType.contains("json") && body != null) {
            body = prettyPrintJson(body);
        }

        return ApiResponseDTO.builder()
                .statusCode(statusCode)
                .statusText(statusText)
                .body(body)
                .bodySize(bodySize)
                .contentType(contentType)
                .headers(headers)
                .responseTimeMs(responseTime)
                .requestUrl(requestUrl)
                .requestMethod(requestMethod)
                .timestamp(LocalDateTime.now())
                .success(true)
                .protocol(response.getVersion() != null ? response.getVersion().toString() : AppConstant.HTTP_1_1)
                .build();
    }

    private ApiResponseDTO buildErrorResponse(ExecuteRequestDTO request, String errorMessage,
                                              long responseTime) {
        return ApiResponseDTO.builder()
                .success(false)
                .errorMessage(errorMessage)
                .errorType("CONNECTION_ERROR")
                .requestUrl(request.getUrl())
                .requestMethod(request.getMethod().name())
                .responseTimeMs(responseTime)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private String prettyPrintJson(String json) {
        try {
            Object jsonObject = objectMapper.readValue(json, Object.class);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
        } catch (JsonProcessingException e) {
            // Return original if not valid JSON
            return json;
        }
    }
}
