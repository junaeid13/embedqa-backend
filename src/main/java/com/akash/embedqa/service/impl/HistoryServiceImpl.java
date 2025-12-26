package com.akash.embedqa.service.impl;

import com.akash.embedqa.enums.HttpMethod;
import com.akash.embedqa.exception.ResourceNotFoundException;
import com.akash.embedqa.model.dtos.response.HistoryRequestDetailDTO;
import com.akash.embedqa.model.dtos.response.HistoryResponseDTO;
import com.akash.embedqa.model.dtos.response.HistoryResponseDetailDTO;
import com.akash.embedqa.model.entities.RequestHistory;
import com.akash.embedqa.repository.RequestHistoryRepository;
import com.akash.embedqa.service.HistoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Implementation of HistoryService for managing API request history.
 * 
 * Author: akash
 * Date: 26/12/25
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final RequestHistoryRepository historyRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public RequestHistory saveHistory(RequestHistory history) {
        log.debug("Saving history entry for URL: {}", history.getUrl());
        return historyRepository.save(history);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HistoryResponseDTO> getHistory(
            HttpMethod method,
            Integer statusCode,
            String search,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Pageable pageable
    ) {
        log.debug("Fetching history with filters - method: {}, statusCode: {}, search: {}",
                method, statusCode, search);

        Specification<RequestHistory> spec = buildSpecification(method, statusCode, search, fromDate, toDate);
        Page<RequestHistory> historyPage = historyRepository.findAll(spec, pageable);

        return historyPage.map(this::mapToSummaryDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public HistoryResponseDTO getById(Long id) {
        log.debug("Fetching history entry by ID: {}", id);

        RequestHistory history = historyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("History entry", id));

        return mapToDetailDTO(history);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting history entry: {}", id);

        if (!historyRepository.existsById(id)) {
            throw new ResourceNotFoundException("History entry", id);
        }
        historyRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void clearAll() {
        log.info("Clearing all history entries");
        historyRepository.deleteAllHistory();
    }

    @Override
    @Transactional(readOnly = true)
    public HistoryStats getStats() {
        log.debug("Calculating history statistics");

        List<RequestHistory> allHistory = historyRepository.findAll();

        if (allHistory.isEmpty()) {
            return new HistoryStats(0L, 0L, 0L, 0.0, Collections.emptyMap());
        }

        long totalRequests = allHistory.size();
        long successCount = allHistory.stream()
                .filter(h -> h.getStatusCode() != null && h.getStatusCode() >= 200 && h.getStatusCode() < 400)
                .count();
        long errorCount = allHistory.stream()
                .filter(h -> h.getStatusCode() != null && h.getStatusCode() >= 400)
                .count();

        double avgResponseTime = allHistory.stream()
                .filter(h -> h.getResponseTime() != null)
                .mapToLong(RequestHistory::getResponseTime)
                .average()
                .orElse(0.0);

        Map<String, Long> methodBreakdown = new HashMap<>();
        for (RequestHistory history : allHistory) {
            if (history.getMethod() != null) {
                String methodName = history.getMethod().name();
                methodBreakdown.merge(methodName, 1L, Long::sum);
            }
        }

        return new HistoryStats(totalRequests, successCount, errorCount, avgResponseTime, methodBreakdown);
    }

    @Override
    @Transactional
    public void deleteOlderThan(int days) {
        log.info("Deleting history entries older than {} days", days);

        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        historyRepository.deleteOlderThan(cutoffDate);
    }

    private Specification<RequestHistory> buildSpecification(
            HttpMethod method,
            Integer statusCode,
            String search,
            LocalDateTime fromDate,
            LocalDateTime toDate
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by HTTP method
            if (method != null) {
                predicates.add(criteriaBuilder.equal(root.get("method"), method));
            }

            // Filter by status code
            // Frontend sends 200 for success (< 400) and 400 for errors (>= 400)
            if (statusCode != null) {
                if (statusCode == 200) {
                    // Success: status codes 200-399
                    predicates.add(criteriaBuilder.and(
                            criteriaBuilder.greaterThanOrEqualTo(root.get("statusCode"), 200),
                            criteriaBuilder.lessThan(root.get("statusCode"), 400)
                    ));
                } else if (statusCode >= 400) {
                    // Errors: status codes >= 400
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("statusCode"), 400));
                } else {
                    // Exact match for other cases
                    predicates.add(criteriaBuilder.equal(root.get("statusCode"), statusCode));
                }
            }

            // Search in URL
            if (search != null && !search.isBlank()) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("url")),
                        searchPattern
                ));
            }

            // Filter by date range
            if (fromDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("executedAt"), fromDate));
            }
            if (toDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("executedAt"), toDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private HistoryResponseDTO mapToSummaryDTO(RequestHistory history) {
        String requestName = null;
        String collectionName = null;

        // Get names from related entities if available
        if (history.getApiRequest() != null) {
            requestName = history.getApiRequest().getName();
            if (history.getApiRequest().getCollection() != null) {
                collectionName = history.getApiRequest().getCollection().getName();
            }
        }

        // Fallback to collection directly on history
        if (collectionName == null && history.getCollection() != null) {
            collectionName = history.getCollection().getName();
        }

        return HistoryResponseDTO.builder()
                .id(history.getId())
                .url(history.getUrl())
                .method(history.getMethod())
                .statusCode(history.getStatusCode())
                .statusText(history.getStatusText())
                .responseTime(history.getResponseTime())
                .responseSize(history.getResponseSize())
                .executedAt(history.getExecutedAt())
                .requestName(requestName)
                .collectionName(collectionName)
                .build();
    }

    private HistoryResponseDTO mapToDetailDTO(RequestHistory history) {
        HistoryResponseDTO dto = mapToSummaryDTO(history);

        // Add request details
        HistoryRequestDetailDTO requestDetail = HistoryRequestDetailDTO.builder()
                .url(history.getUrl())
                .method(history.getMethod())
                .headers(parseJsonToMap(history.getRequestHeaders()))
                .queryParams(parseJsonToMap(history.getQueryParams()))
                .body(history.getRequestBody())
                .bodyType(history.getBodyType())
                .authType(history.getAuthType())
                .authConfig(parseJsonToMap(history.getAuthConfig()))
                .build();

        // Add response details
        HistoryResponseDetailDTO responseDetail = HistoryResponseDetailDTO.builder()
                .statusCode(history.getStatusCode())
                .statusText(history.getStatusText())
                .headers(parseJsonToMap(history.getResponseHeaders()))
                .body(history.getResponseBody())
                .responseTime(history.getResponseTime())
                .responseSize(history.getResponseSize())
                .build();

        dto.setRequest(requestDetail);
        dto.setResponse(responseDetail);

        return dto;
    }

    private Map<String, String> parseJsonToMap(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }

        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse JSON to map: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }
}
