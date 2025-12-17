package com.akash.embedqa.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Author: akash
 * Date: 29/10/25
 */

@Getter
@AllArgsConstructor
public enum BodyType {
    JSON("application/json"),
    XML("application/xml"),
    FORM_DATA("application/x-www-form-urlencoded"),
    RAW("text/plain"),
    BINARY("application/octet-stream");

    private final String contentType;

    BodyType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }
}
