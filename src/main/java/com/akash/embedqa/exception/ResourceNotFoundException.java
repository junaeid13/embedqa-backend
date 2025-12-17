package com.akash.embedqa.exception;

/**
 * Author: akash
 * Date: 17/12/25
 */
public class ResourceNotFoundException extends EmbedQAException {

    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s not found with id: %d", resourceName, id), "NOT_FOUND");
    }

    public ResourceNotFoundException(String resourceName, String identifier) {
        super(String.format("%s not found: %s", resourceName, identifier), "NOT_FOUND");
    }

    public ResourceNotFoundException(String message) {
        super(message, "NOT_FOUND");
    }
}
