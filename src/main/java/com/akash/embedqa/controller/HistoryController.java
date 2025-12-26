package com.akash.embedqa.controller;

import com.akash.embedqa.enums.HttpMethod;
import com.akash.embedqa.model.dtos.response.*;
import com.akash.embedqa.service.HistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Author: akash
 * Date: 26/12/25
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/history")
@RequiredArgsConstructor
@Tag(name = "History", description = "API request execution history management")
public class HistoryController {

    private final HistoryService historyService;


    @GetMapping
    @Operation(
            summary = "Get request history",
            description = "Retrieve paginated request history with optional filtering by method, status, search text, and date range"
    )
    public ResponseEntity<PagedApiResultDTO<HistoryResponseDTO>> getHistory(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "Filter by HTTP method")
            @RequestParam(required = false) HttpMethod method,

            @Parameter(description = "Filter by status code (200 for success < 400, 400 for errors >= 400)")
            @RequestParam(required = false) Integer statusCode,

            @Parameter(description = "Search in URL")
            @RequestParam(required = false) String search,

            @Parameter(description = "Filter from date (ISO format)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,

            @Parameter(description = "Filter to date (ISO format)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate
    ) {
        log.debug("Fetching history - page: {}, size: {}, method: {}, statusCode: {}, search: {}",
                page, size, method, statusCode, search);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "executedAt"));
        Page<HistoryResponseDTO> historyPage = historyService.getHistory(
                method, statusCode, search, fromDate, toDate, pageable
        );

        PagedApiResultDTO<HistoryResponseDTO> result = new PagedApiResultDTO<>();
        result.setSuccess(true);
        result.setData(historyPage.getContent());
        result.setPageInfo(PageInfoDTO.builder()
                .page(historyPage.getNumber())
                .size(historyPage.getSize())
                .totalElements(historyPage.getTotalElements())
                .totalPages(historyPage.getTotalPages())
                .first(historyPage.isFirst())
                .last(historyPage.isLast())
                .build());

        return ResponseEntity.ok(result);
    }

    /**
     * Get a single history entry by ID with full request/response details.
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Get history entry by ID",
            description = "Retrieve a single history entry with complete request and response details"
    )
    public ResponseEntity<ApiResult<HistoryResponseDTO>> getById(
            @Parameter(description = "History entry ID")
            @PathVariable Long id
    ) {
        log.debug("Fetching history entry: {}", id);

        HistoryResponseDTO history = historyService.getById(id);
        return ResponseEntity.ok(ApiResult.success(history));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete history entry",
            description = "Delete a single history entry by ID"
    )
    public ResponseEntity<ApiResult<Void>> delete(
            @Parameter(description = "History entry ID")
            @PathVariable Long id
    ) {
        log.info("Deleting history entry: {}", id);

        historyService.deleteById(id);
        return ResponseEntity.ok(ApiResult.success(null, "History entry deleted successfully"));
    }

    @DeleteMapping
    @Operation(
            summary = "Clear all history",
            description = "Delete all history entries"
    )
    public ResponseEntity<ApiResult<Void>> clearAll() {
        log.info("Clearing all history");

        historyService.clearAll();
        return ResponseEntity.ok(ApiResult.success(null, "All history cleared successfully"));
    }

    @GetMapping("/stats")
    @Operation(
            summary = "Get history statistics",
            description = "Retrieve statistics about request history including total requests, success/error counts, and method breakdown"
    )
    public ResponseEntity<ApiResult<HistoryStatsDTO>> getStats() {
        log.debug("Fetching history statistics");

        HistoryService.HistoryStats stats = historyService.getStats();
        
        HistoryStatsDTO dto = new HistoryStatsDTO(
                stats.totalRequests(),
                stats.successCount(),
                stats.errorCount(),
                stats.avgResponseTime(),
                stats.methodBreakdown()
        );

        return ResponseEntity.ok(ApiResult.success(dto));
    }

    @DeleteMapping("/older-than/{days}")
    @Operation(
            summary = "Delete old history",
            description = "Delete history entries older than the specified number of days"
    )
    public ResponseEntity<ApiResult<Void>> deleteOlderThan(
            @Parameter(description = "Number of days")
            @PathVariable int days
    ) {
        log.info("Deleting history older than {} days", days);

        historyService.deleteOlderThan(days);
        return ResponseEntity.ok(ApiResult.success(null, 
                String.format("History older than %d days deleted successfully", days)));
    }

}
