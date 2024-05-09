package com.weighbridge.SalesManagement.exception;

public class ResourceRetrievalException extends RuntimeException {
    public ResourceRetrievalException(String message, Exception cause) {
        super(message, cause);
    }
}
