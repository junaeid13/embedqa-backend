package com.akash.embedqa.model.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Author: akash
 * Date: 26/12/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryResponseDetailDTO {
    private Integer statusCode;
    private String statusText;
    private Map<String, String> headers;
    private String body;
    private Long responseTime;
    private Long responseSize;
}
