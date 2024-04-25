package com.weighbridge.admin.exceptions;

public class ResourceRetrievalException extends RuntimeException {
    public ResourceRetrievalException(String message, Exception cause) {
        super(message, cause);
    }
}
