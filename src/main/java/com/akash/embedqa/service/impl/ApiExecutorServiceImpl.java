package com.akash.embedqa.service.impl;

import com.akash.embedqa.constant.AppConstant;
import com.akash.embedqa.enums.AuthType;
import com.akash.embedqa.enums.BodyType;
import com.akash.embedqa.enums.HttpMethod;
import com.akash.embedqa.model.dtos.request.AuthConfigDTO;
import com.akash.embedqa.model.dtos.request.ExecuteRequestDTO;
import com.akash.embedqa.model.dtos.request.KeyValuePairDTO;
import com.akash.embedqa.model.dtos.response.ApiResponseDTO;
import com.akash.embedqa.service.ApiExecutorService;
import com.akash.embedqa.service.EnvironmentService;
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

    // Pattern to match environment variables: {{variableName}}
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{([^}]+)}}");

    @Override
    public ApiResponseDTO executeRequest(ExecuteRequestDTO request) {
        return executeAndSave(request, false);
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

            return httpClient.execute(httpRequest, response -> {
                long responseTime = System.currentTimeMillis() - startTime;
                return buildResponse(response, responseTime, uri.toString(), request.getMethod().name());
            });

        } catch (URISyntaxException e) {
            log.error("Invalid URL: {}", request.getUrl(), e);
            return buildErrorResponse(request, "Invalid URL: " + e.getMessage(),
                    System.currentTimeMillis() - startTime);
        } catch (IOException e) {
            log.error("Request execution failed", e);
            return buildErrorResponse(request, "Connection failed: " + e.getMessage(),
                    System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("Unexpected error during request execution", e);
            return buildErrorResponse(request, "Unexpected error: " + e.getMessage(),
                    System.currentTimeMillis() - startTime);
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
