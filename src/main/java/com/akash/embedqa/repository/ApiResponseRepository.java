package com.akash.embedqa.repository;

import com.akash.embedqa.model.entities.ApiResponse;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Author: akash
 * Date: 29/10/25
 */
public interface ApiResponseRepository extends JpaRepository<ApiResponse, Long> {
}
