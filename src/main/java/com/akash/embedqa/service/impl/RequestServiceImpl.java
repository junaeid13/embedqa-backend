package com.akash.embedqa.service.impl;

import com.akash.embedqa.exception.ResourceNotFoundException;
import com.akash.embedqa.model.dtos.request.KeyValuePairDTO;
import com.akash.embedqa.model.dtos.request.SaveRequestDTO;
import com.akash.embedqa.model.dtos.response.RequestDetailDTO;
import com.akash.embedqa.model.dtos.response.RequestSummaryDTO;
import com.akash.embedqa.model.entities.*;
import com.akash.embedqa.repository.ApiCollectionRepository;
import com.akash.embedqa.repository.ApiRequestRepository;
import com.akash.embedqa.repository.EnvironmentRepository;
import com.akash.embedqa.service.RequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: akash
 * Date: 17/12/25
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final ApiRequestRepository requestRepository;
    private final ApiCollectionRepository collectionRepository;
    private final EnvironmentRepository environmentRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public RequestDetailDTO save(SaveRequestDTO dto) {
        log.debug("Saving request: {}", dto.getName());

        // Get or create collection
        ApiCollection collection = resolveCollection(dto);

        // Get environment if specified
        Environment environment = null;
        if (dto.getEnvironmentId() != null) {
            environment = environmentRepository.findById(dto.getEnvironmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Environment", dto.getEnvironmentId()));
        }

        ApiRequest request = ApiRequest.builder()
                .name(dto.getName())
                .url(dto.getUrl())
                .method(dto.getMethod())
                .description(dto.getDescription())
                .requestBody(dto.getBody())
                .bodyType(dto.getBodyType())
                .authType(dto.getAuthType())
                .authConfig(dto.getAuthConfig())
                .collection(collection)
                .environment(environment)
                .build();

        // Add headers (only enabled ones with non-blank keys)
        if (dto.getHeaders() != null) {
            for (KeyValuePairDTO headerDto : dto.getHeaders()) {
                if (isValidKeyValuePair(headerDto)) {
                    RequestHeader header = RequestHeader.builder()
                            .headerName(headerDto.getKey())
                            .headerValue(headerDto.getValue() != null ? headerDto.getValue() : "")
                            .build();
                    request.addHeader(header);
                }
            }
        }

        // Add query parameters (only enabled ones with non-blank keys)
        if (dto.getQueryParams() != null) {
            for (KeyValuePairDTO paramDto : dto.getQueryParams()) {
                if (isValidKeyValuePair(paramDto)) {
                    QueryParameter param = QueryParameter.builder()
                            .name(paramDto.getKey())
                            .value(paramDto.getValue() != null ? paramDto.getValue() : "")
                            .build();
                    request.addQueryParam(param);
                }
            }
        }

        ApiRequest saved = requestRepository.save(request);
        log.info("Request saved successfully with ID: {}", saved.getId());
        return mapToDetail(saved);
    }

    private ApiCollection resolveCollection(SaveRequestDTO dto) {
        // If collectionId is provided, use existing collection
        if (dto.getCollectionId() != null) {
            return collectionRepository.findById(dto.getCollectionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Collection", dto.getCollectionId()));
        }

        // If newCollectionName is provided, create new collection
        if (dto.getNewCollectionName() != null && !dto.getNewCollectionName().isBlank()) {
            log.debug("Creating new collection: {}", dto.getNewCollectionName());
            ApiCollection newCollection = ApiCollection.builder()
                    .name(dto.getNewCollectionName().trim())
                    .description(dto.getNewCollectionDescription())
                    .build();
            return collectionRepository.save(newCollection);
        }

        // No collection specified
        return null;
    }

    private boolean isValidKeyValuePair(KeyValuePairDTO kvp) {
        return kvp != null
                && Boolean.TRUE.equals(kvp.getEnabled())
                && kvp.getKey() != null
                && !kvp.getKey().isBlank();
    }

    @Override
    @Transactional
    public RequestDetailDTO update(Long id, SaveRequestDTO dto) {
        log.debug("Updating request: {}", id);

        ApiRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request", id));

        request.setName(dto.getName());
        request.setUrl(dto.getUrl());
        request.setMethod(dto.getMethod());
        request.setDescription(dto.getDescription());
        request.setRequestBody(dto.getBody());
        request.setBodyType(dto.getBodyType());
        request.setAuthType(dto.getAuthType());
        request.setAuthConfig(dto.getAuthConfig());

        // Update collection
        ApiCollection collection = resolveCollection(dto);
        request.setCollection(collection);

        // Update environment if specified
        if (dto.getEnvironmentId() != null) {
            Environment environment = environmentRepository.findById(dto.getEnvironmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Environment", dto.getEnvironmentId()));
            request.setEnvironment(environment);
        } else {
            request.setEnvironment(null);
        }

        // Clear and re-add headers
        request.getHeaders().clear();
        if (dto.getHeaders() != null) {
            for (KeyValuePairDTO headerDto : dto.getHeaders()) {
                if (isValidKeyValuePair(headerDto)) {
                    RequestHeader header = RequestHeader.builder()
                            .headerName(headerDto.getKey())
                            .headerValue(headerDto.getValue() != null ? headerDto.getValue() : "")
                            .build();
                    request.addHeader(header);
                }
            }
        }

        // Clear and re-add query parameters
        request.getQueryParams().clear();
        if (dto.getQueryParams() != null) {
            for (KeyValuePairDTO paramDto : dto.getQueryParams()) {
                if (isValidKeyValuePair(paramDto)) {
                    QueryParameter param = QueryParameter.builder()
                            .name(paramDto.getKey())
                            .value(paramDto.getValue() != null ? paramDto.getValue() : "")
                            .build();
                    request.addQueryParam(param);
                }
            }
        }

        ApiRequest saved = requestRepository.save(request);
        log.info("Request updated successfully: {}", saved.getId());
        return mapToDetail(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RequestDetailDTO getById(Long id) {
        ApiRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request", id));
        return mapToDetail(request);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RequestSummaryDTO> getAll(Pageable pageable) {
        Page<ApiRequest> requests = requestRepository.findAll(pageable);
        List<RequestSummaryDTO> summaries = requests.getContent().stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());
        return new PageImpl<>(summaries, pageable, requests.getTotalElements());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Deleting request: {}", id);

        if (!requestRepository.existsById(id)) {
            throw new ResourceNotFoundException("Request", id);
        }
        requestRepository.deleteById(id);
    }

    private RequestDetailDTO mapToDetail(ApiRequest request) {
        List<KeyValuePairDTO> headers = request.getHeaders().stream()
                .map(h -> KeyValuePairDTO.builder()
                        .key(h.getHeaderName())
                        .value(h.getHeaderValue())
                        .enabled(true)
                        .build())
                .collect(Collectors.toList());

        List<KeyValuePairDTO> queryParams = request.getQueryParams().stream()
                .map(p -> KeyValuePairDTO.builder()
                        .key(p.getName())
                        .value(p.getValue())
                        .enabled(true)
                        .build())
                .collect(Collectors.toList());

        return RequestDetailDTO.builder()
                .id(request.getId())
                .name(request.getName())
                .url(request.getUrl())
                .method(request.getMethod())
                .description(request.getDescription())
                .headers(headers)
                .queryParams(queryParams)
                .body(request.getRequestBody())
                .bodyType(request.getBodyType())
                .authType(request.getAuthType())
                .authConfig(request.getAuthConfig())
                .collectionId(request.getCollection() != null ? request.getCollection().getId() : null)
                .collectionName(request.getCollection() != null ? request.getCollection().getName() : null)
                .environmentId(request.getEnvironment() != null ? request.getEnvironment().getId() : null)
                .environmentName(request.getEnvironment() != null ? request.getEnvironment().getName() : null)
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .createdBy(request.getCreatedBy())
                .updatedBy(request.getUpdatedBy())
                .build();
    }

    private RequestSummaryDTO mapToSummary(ApiRequest request) {
        return RequestSummaryDTO.builder()
                .id(request.getId())
                .name(request.getName())
                .url(request.getUrl())
                .method(request.getMethod())
                .description(request.getDescription())
                .collectionId(request.getCollection() != null ? request.getCollection().getId() : null)
                .collectionName(request.getCollection() != null ? request.getCollection().getName() : null)
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }
}
