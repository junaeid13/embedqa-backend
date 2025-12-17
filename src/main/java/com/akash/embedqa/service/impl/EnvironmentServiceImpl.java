package com.akash.embedqa.service.impl;

import com.akash.embedqa.exception.ResourceNotFoundException;
import com.akash.embedqa.model.dtos.request.EnvironmentDTO;
import com.akash.embedqa.model.dtos.request.EnvironmentVariableDTO;
import com.akash.embedqa.model.dtos.response.EnvironmentResponseDTO;
import com.akash.embedqa.model.entities.Environment;
import com.akash.embedqa.repository.EnvironmentRepository;
import com.akash.embedqa.service.EnvironmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: akash
 * Date: 17/12/25
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class EnvironmentServiceImpl implements EnvironmentService {

    private final EnvironmentRepository environmentRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public EnvironmentResponseDTO create(EnvironmentDTO dto) {
        log.debug("Creating environment: {}", dto.getName());

        Environment environment = Environment.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .variablesJson(dto.getVariables())
                .build();

        Environment saved = environmentRepository.save(environment);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public EnvironmentResponseDTO update(Long id, EnvironmentDTO dto) {
        log.debug("Updating environment: {}", id);

        Environment environment = environmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Environment", id));

        environment.setName(dto.getName());
        environment.setDescription(dto.getDescription());
        environment.setVariablesJson(dto.getVariables());

        Environment saved = environmentRepository.save(environment);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public EnvironmentResponseDTO getById(Long id) {
        Environment environment = environmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Environment", id));
        return mapToResponse(environment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnvironmentResponseDTO> getAll() {
        return environmentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Deleting environment: {}", id);

        if (!environmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Environment", id);
        }
        environmentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> getVariablesAsMap(Long environmentId) {
        if (environmentId == null) {
            return Collections.emptyMap();
        }

        Environment environment = environmentRepository.findById(environmentId)
                .orElse(null);

        if (environment == null || environment.getVariablesJson() == null) {
            return Collections.emptyMap();
        }

        List<EnvironmentVariableDTO> variables = environment.getVariablesJson();

        return variables.stream()
                .filter(v -> Boolean.TRUE.equals(v.getEnabled()))
                .collect(Collectors.toMap(
                        EnvironmentVariableDTO::getName,
                        v -> v.getValue() != null ? v.getValue() : "",
                        (v1, v2) -> v2  // Keep last value if duplicate keys
                ));
    }

    private EnvironmentResponseDTO mapToResponse(Environment environment) {
        return EnvironmentResponseDTO.builder()
                .id(environment.getId())
                .name(environment.getName())
                .description(environment.getDescription())
                .variables(environment.getVariablesJson())
                .active(false)
                .createdAt(environment.getCreatedAt())
                .updatedAt(environment.getUpdatedAt())
                .build();
    }
}
