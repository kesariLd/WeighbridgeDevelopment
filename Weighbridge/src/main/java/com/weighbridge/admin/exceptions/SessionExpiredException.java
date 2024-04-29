package com.weighbridge.admin.exceptions;

public class SessionExpiredException extends RuntimeException{

    public SessionExpiredException(String message) {
        super(message);
    }
}
