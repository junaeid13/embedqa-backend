package com.akash.embedqa.model.dtos.response;

import com.akash.embedqa.enums.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Author: akash
 * Date: 17/12/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestSummaryDTO {

    private Long id;
    private String name;
    private String url;
    private HttpMethod method;
    private String description;
    private Long collectionId;
    private String collectionName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastExecutedAt;
}
