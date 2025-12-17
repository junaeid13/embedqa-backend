package com.akash.embedqa.model.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: akash
 * Date: 17/12/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionDTO {

    private Long id;

    @NotBlank(message = "Collection name is required")
    private String name;

    private String description;

    // Parent collection ID for folder organization
    private Long parentId;
}
