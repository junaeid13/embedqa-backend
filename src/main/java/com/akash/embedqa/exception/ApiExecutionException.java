package com.akash.embedqa.exception;

/**
 * Author: akash
 * Date: 17/12/25
 */
public class ApiExecutionException extends EmbedQAException {

    public ApiExecutionException(String message) {
        super(message, "API_EXECUTION_ERROR");
    }

    public ApiExecutionException(String message, Throwable cause) {
        super(message, "API_EXECUTION_ERROR", cause);
    }
}
