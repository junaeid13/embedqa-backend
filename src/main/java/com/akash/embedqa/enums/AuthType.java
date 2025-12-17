package com.akash.embedqa.enums;

import com.akash.embedqa.constant.AppConstant;
import com.akash.embedqa.model.dtos.request.AuthConfigDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 * Author: akash
 * Date: 29/10/25
 */
@Slf4j
public enum AuthType {

    NONE {
        @Override
        public void apply(HttpUriRequestBase request, AuthConfigDTO config, Map<String, String> vars) {
            // Do nothing
        }
    },

    BEARER_TOKEN {
        @Override
        public void apply(HttpUriRequestBase request, AuthConfigDTO config, Map<String, String> vars) {
            String token = resolve(config.getBearerToken(), vars);
            if (token != null && !token.isBlank()) {
                request.addHeader(AppConstant.AUTHORIZATION, AppConstant.BEARER + token);
            }
        }
    },

    BASIC_AUTH {
        @Override
        public void apply(HttpUriRequestBase request, AuthConfigDTO config, Map<String, String> vars) {
            String username = resolve(config.getBasicUsername(), vars);
            String password = resolve(config.getBasicPassword(), vars);
            if (username != null && password != null) {
                String encoded = Base64.getEncoder()
                        .encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
                request.addHeader(AppConstant.AUTHORIZATION, AppConstant.BASIC + encoded);
            }
        }
    },

    API_KEY {
        @Override
        public void apply(HttpUriRequestBase request, AuthConfigDTO config, Map<String, String> vars) {
            String apiKey = resolve(config.getApiKey(), vars);
            if (apiKey == null || apiKey.isBlank()) return;

            String headerName = config.getApiKeyHeaderName();
            if (headerName == null || headerName.isBlank()) headerName = AppConstant.X_API_KEY;

            String location = config.getApiKeyLocation();
            if ("query".equalsIgnoreCase(location)) {
                // query param not implemented yet
                log.warn("API key in query parameter not supported, using header");
            }

            request.addHeader(headerName, apiKey);
        }
    },

    OAUTH2 {
        @Override
        public void apply(HttpUriRequestBase request, AuthConfigDTO config, Map<String, String> vars) {
            String accessToken = resolve(config.getOauth2AccessToken(), vars);
            if (accessToken != null && !accessToken.isBlank()) {
                request.addHeader(AppConstant.AUTHORIZATION, AppConstant.BEARER + accessToken);
            }
        }
    };

    public abstract void apply(HttpUriRequestBase request, AuthConfigDTO config, Map<String, String> vars);

    // Helper to resolve variables
    protected String resolve(String value, Map<String, String> vars) {
        return value != null && vars != null ? vars.getOrDefault(value, value) : value;
    }
}
