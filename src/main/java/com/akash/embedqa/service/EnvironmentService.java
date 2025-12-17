package com.akash.embedqa.service;

import com.akash.embedqa.model.dtos.request.EnvironmentDTO;
import com.akash.embedqa.model.dtos.response.EnvironmentResponseDTO;

import java.util.List;
import java.util.Map;

/**
 * Author: akash
 * Date: 17/12/25
 */
public interface EnvironmentService {
    EnvironmentResponseDTO create(EnvironmentDTO dto);
    EnvironmentResponseDTO update(Long id, EnvironmentDTO dto);
    EnvironmentResponseDTO getById(Long id);
    List<EnvironmentResponseDTO> getAll();
    void delete(Long id);
    Map<String, String> getVariablesAsMap(Long environmentId);
}
