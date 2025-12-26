package com.akash.embedqa.model.dtos.response;

import com.akash.embedqa.enums.HttpMethod;
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
public class HistoryRequestDetailDTO {
    private String url;
    private HttpMethod method;
    private Map<String, String> headers;
    private Map<String, String> queryParams;
    private String body;
    private String bodyType;
    private String authType;
    private Map<String, String> authConfig;
}
