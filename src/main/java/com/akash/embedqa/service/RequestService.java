package com.akash.embedqa.service;

import com.akash.embedqa.model.dtos.request.SaveRequestDTO;
import com.akash.embedqa.model.dtos.response.RequestDetailDTO;
import com.akash.embedqa.model.dtos.response.RequestSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Author: akash
 * Date: 17/12/25
 */
public interface RequestService {

    RequestDetailDTO save(SaveRequestDTO dto);
    RequestDetailDTO update(Long id, SaveRequestDTO dto);
    RequestDetailDTO getById(Long id);
    Page<RequestSummaryDTO> getAll(Pageable pageable);
    void delete(Long id);

}
