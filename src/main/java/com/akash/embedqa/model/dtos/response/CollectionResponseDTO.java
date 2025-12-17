package com.akash.embedqa.model.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Author: akash
 * Date: 17/12/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionResponseDTO {

    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private Integer requestCount;
    private List<RequestSummaryDTO> requests;
    private List<CollectionResponseDTO> subCollections;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
