package com.weighbridge.qualityuser.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceAlreadyExistsException extends RuntimeException{
    private String resourceName;
    private String fieldName;
    private Object fieldValue;
    private String message;

    public ResourceAlreadyExistsException(String resourceName, String fieldName, Object fieldValue, String message) {
        super(String.format("%s already exists with %s : \"%s\" %s", resourceName, fieldName, fieldValue, message));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.message = message;
    }
}
