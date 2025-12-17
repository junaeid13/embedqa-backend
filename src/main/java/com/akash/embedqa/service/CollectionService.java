package com.akash.embedqa.service;

import com.akash.embedqa.model.dtos.request.CollectionDTO;
import com.akash.embedqa.model.dtos.response.CollectionResponseDTO;

import java.util.List;

/**
 * Author: akash
 * Date: 17/12/25
 */
public interface CollectionService {

    CollectionResponseDTO create(CollectionDTO dto);
    List<CollectionResponseDTO> getAll();
    CollectionResponseDTO getById(Long id);
    CollectionResponseDTO update(Long id, CollectionDTO dto);
    void delete(Long id);

}
