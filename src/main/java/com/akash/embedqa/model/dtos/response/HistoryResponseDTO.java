package com.akash.embedqa.model.dtos.response;

import com.akash.embedqa.enums.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryResponseDTO {

    private Long id;
    private String url;
    private HttpMethod method;
    private Integer statusCode;
    private String statusText;
    private Long responseTime;
    private Long responseSize;
    private LocalDateTime executedAt;

    private String requestName;
    private String collectionName;

    // Detailed request info (loaded on demand)
    private HistoryRequestDetailDTO request;
    private HistoryResponseDetailDTO response;
}
