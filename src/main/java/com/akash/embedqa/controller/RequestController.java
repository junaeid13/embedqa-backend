package com.akash.embedqa.controller;

import com.akash.embedqa.model.dtos.request.SaveRequestDTO;
import com.akash.embedqa.model.dtos.response.ApiResult;
import com.akash.embedqa.model.dtos.response.RequestDetailDTO;
import com.akash.embedqa.model.dtos.response.RequestSummaryDTO;
import com.akash.embedqa.service.RequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Author: akash
 * Date: 17/12/25
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/requests")
@RequiredArgsConstructor
@Tag(name = "Requests", description = "Manage saved API requests")
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    @Operation(summary = "Save request", description = "Save a new API request")
    public ResponseEntity<ApiResult<RequestDetailDTO>> save(
            @Valid @RequestBody SaveRequestDTO dto) {
        log.info("Saving request: {}", dto.getName());

        RequestDetailDTO saved = requestService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResult.success(saved, "Request saved successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all requests", description = "Retrieve all saved requests with pagination")
    public ResponseEntity<ApiResult<Page<RequestSummaryDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<RequestSummaryDTO> requests = requestService.getAll(pageable);
        return ResponseEntity.ok(ApiResult.success(requests));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get request by ID", description = "Retrieve a request with full details")
    public ResponseEntity<ApiResult<RequestDetailDTO>> getById(@PathVariable Long id) {
        RequestDetailDTO request = requestService.getById(id);
        return ResponseEntity.ok(ApiResult.success(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update request", description = "Update an existing request")
    public ResponseEntity<ApiResult<RequestDetailDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody SaveRequestDTO dto) {
        log.info("Updating request: {}", id);

        RequestDetailDTO updated = requestService.update(id, dto);
        return ResponseEntity.ok(ApiResult.success(updated, "Request updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete request", description = "Delete a saved request")
    public ResponseEntity<ApiResult<Void>> delete(@PathVariable Long id) {
        log.info("Deleting request: {}", id);

        requestService.delete(id);
        return ResponseEntity.ok(ApiResult.success(null, "Request deleted successfully"));
    }
}
