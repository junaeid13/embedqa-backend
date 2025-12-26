package com.akash.embedqa.service;

import com.akash.embedqa.enums.HttpMethod;
import com.akash.embedqa.model.dtos.response.HistoryResponseDTO;
import com.akash.embedqa.model.entities.RequestHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Author: akash
 * Date: 26/12/25
 */
public interface HistoryService {

    RequestHistory saveHistory(RequestHistory history);

    Page<HistoryResponseDTO> getHistory(
            HttpMethod method,
            Integer statusCode,
            String search,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Pageable pageable
    );

    HistoryResponseDTO getById(Long id);

    void deleteById(Long id);

    void clearAll();

    HistoryStats getStats();

    void deleteOlderThan(int days);

    record HistoryStats(
            Long totalRequests,
            Long successCount,
            Long errorCount,
            Double avgResponseTime,
            Map<String, Long> methodBreakdown
    ) {}
}
