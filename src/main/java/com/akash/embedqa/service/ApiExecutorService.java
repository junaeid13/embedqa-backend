package com.akash.embedqa.service;

import com.akash.embedqa.model.dtos.request.ExecuteRequestDTO;
import com.akash.embedqa.model.dtos.response.ApiResponseDTO;

/**
 * Author: akash
 * Date: 17/12/25
 */
public interface ApiExecutorService {

    ApiResponseDTO executeRequest(ExecuteRequestDTO request);
    ApiResponseDTO executeAndSave(ExecuteRequestDTO request, boolean saveToHistory);
}
