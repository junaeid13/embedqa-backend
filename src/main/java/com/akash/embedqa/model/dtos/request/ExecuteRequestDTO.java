package com.akash.embedqa.model.dtos.request;

import com.akash.embedqa.enums.AuthType;
import com.akash.embedqa.enums.BodyType;
import com.akash.embedqa.enums.HttpMethod;
import jakarta.validation.Valid;
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
 * Date: 17/12/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecuteRequestDTO {

    @NotBlank(message = "URL is required")
    private String url;

    @NotNull(message = "HTTP method is required")
    private HttpMethod method;

    @Valid
    @Builder.Default
    private List<KeyValuePairDTO> headers = new ArrayList<>();

    @Valid
    @Builder.Default
    private List<KeyValuePairDTO> queryParams = new ArrayList<>();

    @Valid
    @Builder.Default
    private List<KeyValuePairDTO> formData = new ArrayList<>();

    private String body;

    @Builder.Default
    private BodyType bodyType = BodyType.NONE;

    @Builder.Default
    private AuthType authType = AuthType.NONE;

    private AuthConfigDTO authConfig;

    // Optional: Environment ID for variable substitution
    private Long environmentId;

    // Optional: Request name for saving
    private String name;

    // Optional: Description
    private String description;

    // Optional: Collection ID for saving
    private Long collectionId;

    // Timeout in milliseconds (default: 30000)
    @Builder.Default
    private Integer timeout = 30000;

    // Follow redirects
    @Builder.Default
    private Boolean followRedirects = true;

    // Verify SSL certificates
    @Builder.Default
    private Boolean verifySsl = true;
}

