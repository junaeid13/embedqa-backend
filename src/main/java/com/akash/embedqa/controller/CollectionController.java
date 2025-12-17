package com.akash.embedqa.controller;

import com.akash.embedqa.model.dtos.request.CollectionDTO;
import com.akash.embedqa.model.dtos.response.ApiResult;
import com.akash.embedqa.model.dtos.response.CollectionResponseDTO;
import com.akash.embedqa.service.CollectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Author: akash
 * Date: 17/12/25
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/collections")
@RequiredArgsConstructor
@Tag(name = "Collections", description = "Manage API request collections")
public class CollectionController {

    private final CollectionService collectionService;

    @PostMapping
    @Operation(summary = "Create collection", description = "Create a new API collection")
    public ResponseEntity<ApiResult<CollectionResponseDTO>> create(
            @Valid @RequestBody CollectionDTO dto) {
        log.info("Creating collection: {}", dto.getName());

        CollectionResponseDTO created = collectionService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResult.success(created, "Collection created successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all collections", description = "Retrieve all collections")
    public ResponseEntity<ApiResult<List<CollectionResponseDTO>>> getAll() {
        List<CollectionResponseDTO> collections = collectionService.getAll();
        return ResponseEntity.ok(ApiResult.success(collections));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get collection by ID", description = "Retrieve a collection with all its requests")
    public ResponseEntity<ApiResult<CollectionResponseDTO>> getById(@PathVariable Long id) {
        CollectionResponseDTO collection = collectionService.getById(id);
        return ResponseEntity.ok(ApiResult.success(collection));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update collection", description = "Update an existing collection")
    public ResponseEntity<ApiResult<CollectionResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody CollectionDTO dto) {
        log.info("Updating collection: {}", id);

        CollectionResponseDTO updated = collectionService.update(id, dto);
        return ResponseEntity.ok(ApiResult.success(updated, "Collection updated successfully"));
    }


}
