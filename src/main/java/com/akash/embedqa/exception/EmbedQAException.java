package com.akash.embedqa.exception;

import lombok.Getter;

/**
 * Author: akash
 * Date: 17/12/25
 */
@Getter
public class EmbedQAException extends RuntimeException {

    private final String errorCode;

    public EmbedQAException(String message) {
        super(message);
        this.errorCode = "EMBEDQA_ERROR";
    }

    public EmbedQAException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public EmbedQAException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "EMBEDQA_ERROR";
    }

    public EmbedQAException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
