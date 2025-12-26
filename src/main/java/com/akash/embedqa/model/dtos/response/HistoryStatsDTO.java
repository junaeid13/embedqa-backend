package com.akash.embedqa.model.dtos.response;

/**
 * Author: akash
 * Date: 26/12/25
 */
public record HistoryStatsDTO(
        Long totalRequests,
        Long successCount,
        Long errorCount,
        Double avgResponseTime,
        java.util.Map<String, Long> methodBreakdown
) {}
