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
public class SaveRequestDTO {

    @NotBlank(message = "Request name is required")
    private String name;

    @NotBlank(message = "URL is required")
    private String url;

    @NotNull(message = "HTTP method is required")
    private HttpMethod method;

    private String description;

    @Valid
    @Builder.Default
    private List<KeyValuePairDTO> headers = new ArrayList<>();

    @Valid
    @Builder.Default
    private List<KeyValuePairDTO> queryParams = new ArrayList<>();

    private String body;

    @Builder.Default
    private BodyType bodyType = BodyType.NONE;

    @Builder.Default
    private AuthType authType = AuthType.NONE;

    private AuthConfigDTO authConfig;

    private Long collectionId;

    private Long environmentId;

    private String newCollectionName;

    private String newCollectionDescription;

}
