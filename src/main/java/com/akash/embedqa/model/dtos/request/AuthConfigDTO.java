package com.akash.embedqa.model.dtos.request;

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
    // Bearer Token Authentication
    private String bearerToken;

    // Basic Authentication
    private String basicUsername;
    private String basicPassword;

    // API Key Authentication
    private String apiKey;
    private String apiKeyHeaderName;  // Header name for API key (default: X-API-Key)
    private String apiKeyLocation;    // "header" or "query"

    // OAuth2 Configuration
    private String oauth2AccessToken;
    private String oauth2RefreshToken;
    private String oauth2TokenUrl;
    private String oauth2ClientId;
    private String oauth2ClientSecret;
    private String oauth2Scope;
    private String oauth2GrantType;  // "client_credentials", "password", "authorization_code"
}
