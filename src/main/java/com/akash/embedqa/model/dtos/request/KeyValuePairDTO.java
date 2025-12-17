package com.akash.embedqa.model.dtos.request;

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
public class KeyValuePairDTO {

    private String key;

    private String value;

    // Whether this pair is enabled/active
    @Builder.Default
    private Boolean enabled = true;

    // Optional description for documentation
    private String description;
}
