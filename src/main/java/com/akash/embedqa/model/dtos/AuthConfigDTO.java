package com.akash.embedqa.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: akash
 * Date: 29/10/25
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthConfigDTO {
    private String token;

    private String username;
    private String password;

    private String apiKey;
    private String apiKeyHeader;

    private String accessToken;
    private String tokenType;
}
