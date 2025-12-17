package com.akash.embedqa.model.dtos.response;

import com.akash.embedqa.enums.AuthType;
import com.akash.embedqa.enums.BodyType;
import com.akash.embedqa.enums.HttpMethod;
import com.akash.embedqa.model.dtos.request.AuthConfigDTO;
import com.akash.embedqa.model.dtos.request.KeyValuePairDTO;
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
public class RequestDetailDTO {

    private Long id;
    private String name;
    private String url;
    private HttpMethod method;
    private String description;

    private List<KeyValuePairDTO> headers;
    private List<KeyValuePairDTO> queryParams;

    private String body;
    private BodyType bodyType;

    private AuthType authType;
    private AuthConfigDTO authConfig;

    private Long collectionId;
    private String collectionName;

    private Long environmentId;
    private String environmentName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    // Last response (if available)
    private ApiResponseDTO lastResponse;
}
