package com.akash.embedqa.service.impl;

import com.akash.embedqa.exception.ResourceNotFoundException;
import com.akash.embedqa.model.dtos.request.CollectionDTO;
import com.akash.embedqa.model.dtos.response.CollectionResponseDTO;
import com.akash.embedqa.model.dtos.response.RequestSummaryDTO;
import com.akash.embedqa.model.entities.ApiCollection;
import com.akash.embedqa.model.entities.ApiRequest;
import com.akash.embedqa.repository.ApiCollectionRepository;
import com.akash.embedqa.service.CollectionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class CollectionServiceImpl implements CollectionService {

    private final ApiCollectionRepository collectionRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public CollectionResponseDTO create(CollectionDTO dto) {
        log.debug("Creating collection: {}", dto.getName());

        ApiCollection collection = ApiCollection.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();

        ApiCollection saved = collectionRepository.save(collection);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollectionResponseDTO> getAll() {
        return collectionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponseDTO getById(Long id) {
        ApiCollection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection", id));
        return mapToResponse(collection);
    }

    @Override
    @Transactional
    public CollectionResponseDTO update(Long id, CollectionDTO dto) {
        log.debug("Updating collection: {}", id);

        ApiCollection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection", id));

        collection.setName(dto.getName());
        collection.setDescription(dto.getDescription());

        ApiCollection saved = collectionRepository.save(collection);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Deleting collection: {}", id);

        if (!collectionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Collection", id);
        }
        collectionRepository.deleteById(id);
    }

    private CollectionResponseDTO mapToResponse(ApiCollection collection) {
        List<RequestSummaryDTO> requests = collection.getRequests().stream()
                .map(this::mapRequestToSummary)
                .collect(Collectors.toList());

        return CollectionResponseDTO.builder()
                .id(collection.getId())
                .name(collection.getName())
                .description(collection.getDescription())
                .requestCount(collection.getRequests().size())
                .requests(requests)
                .subCollections(new ArrayList<>())
                .build();
    }

    private RequestSummaryDTO mapRequestToSummary(ApiRequest request) {
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
