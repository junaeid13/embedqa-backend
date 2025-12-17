package com.akash.embedqa.model.dtos;

import com.akash.embedqa.enums.AuthType;
import com.akash.embedqa.enums.BodyType;
import com.akash.embedqa.enums.HttpMethod;
import com.akash.embedqa.model.dtos.request.AuthConfigDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: akash
 * Date: 29/10/25
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiRequestDTO {

    @NotBlank(message = "URL is required")
    private String url;

    @NotNull(message = "HTTP method is required")
    private HttpMethod method;

    @Builder.Default
    private List<RequestHeaderDTO> headers = new ArrayList<>();

    @Builder.Default
    private List<QueryParameterDTO> queryParams = new ArrayList<>();

    private String body;

    @Builder.Default
    private BodyType bodyType = BodyType.NONE;

    @Builder.Default
    private AuthType authType = AuthType.NONE;

    private AuthConfigDTO authConfig;

    private Long environmentId;
}
