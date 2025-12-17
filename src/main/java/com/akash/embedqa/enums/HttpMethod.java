package com.akash.embedqa.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.hc.client5.http.classic.methods.*;

import java.net.URI;

/**
 * Author: akash
 * Date: 29/10/25
 */

@Getter
@AllArgsConstructor
public enum HttpMethod {
    GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS;

    public HttpUriRequestBase create(URI uri) {
        return switch (this) {
            case GET -> new HttpGet(uri);
            case POST -> new HttpPost(uri);
            case PUT -> new HttpPut(uri);
            case DELETE -> new HttpDelete(uri);
            case PATCH -> new HttpPatch(uri);
            case HEAD -> new HttpHead(uri);
            case OPTIONS -> new HttpOptions(uri);
        };
    }

    public boolean supportsBody() {
        return this == POST || this == PUT || this == PATCH;
    }
}
