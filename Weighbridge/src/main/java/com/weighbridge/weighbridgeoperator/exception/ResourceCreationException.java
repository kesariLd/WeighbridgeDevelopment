package com.weighbridge.weighbridgeoperator.exception;

public class ResourceCreationException extends RuntimeException{

    public ResourceCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}