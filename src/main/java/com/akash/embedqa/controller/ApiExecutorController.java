package com.akash.embedqa.controller;

import com.akash.embedqa.model.dtos.request.ExecuteRequestDTO;
import com.akash.embedqa.model.dtos.response.ApiResponseDTO;
import com.akash.embedqa.model.dtos.response.ApiResult;
import com.akash.embedqa.service.ApiExecutorService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author: akash
 * Date: 17/12/25
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/execute")
@RequiredArgsConstructor
public class ApiExecutorController {

    private final ApiExecutorService apiExecutorService;

    @PostMapping
    @Operation(summary = "Execute an API request", description = "Execute an HTTP request and return the response")
    public ResponseEntity<ApiResult<ApiResponseDTO>> executeRequest(
            @Valid @RequestBody ExecuteRequestDTO request) {
        log.info("Executing {} request to: {}", request.getMethod(), request.getUrl());

        ApiResponseDTO response = apiExecutorService.executeRequest(request);

        return ResponseEntity.ok(ApiResult.success(response, "Request executed successfully"));
    }
}
