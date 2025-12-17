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
public class ApiResponseDTO {

    // Response status
    private Integer statusCode;
    private String statusText;

    // Response body
    private String body;
    private String contentType;
    private Long bodySize;  // Size in bytes

    // Response headers
    private List<HeaderDTO> headers;

    // Timing information
    private Long responseTimeMs;
    private Long dnsLookupTimeMs;
    private Long connectionTimeMs;
    private Long tlsHandshakeTimeMs;
    private Long firstByteTimeMs;
    private Long downloadTimeMs;

    // Request info (for reference)
    private String requestUrl;
    private String requestMethod;

    // Metadata
    private LocalDateTime timestamp;
    private String protocol;  // HTTP/1.1, HTTP/2, etc.
    private String remoteAddress;

    // Error info (if request failed)
    private Boolean success;
    private String errorMessage;
    private String errorType;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HeaderDTO {
        private String name;
        private String value;
    }
}
