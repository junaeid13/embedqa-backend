package com.akash.embedqa.model.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: akash
 * Date: 29/10/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueryParamDTO {

    @NotBlank(message = "Query param key is required")
    private String key;

    @NotBlank(message = "Query param value is required")
    private String value;

    @Builder.Default
    private Boolean enabled = true;
}
