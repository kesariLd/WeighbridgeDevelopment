package com.weighbridge.qualityuser.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception class to handle resource not found exception.
 * This class extends RuntimeException and is annotated with @ResponseStatus to set the HTTP status code NOT_FOUND (404).
 */
@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException{

    /**
     * The name of the resource that was not found.
     */
    private String resourceName;

    /**
     * The name of the field that was not found.
     */
    private String fieldName;

    /**
     * The value of the field that was not found.
     */
    private String fieldValue;

    /**
     * Constructor for ResourceNotFoundException.
     *
     * @param message The custom message that describes the exception.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor for ResourceNotFoundException with resource name, field name and field value.
     *
     * @param resourceName The name of the resource that was not found.
     * @param fieldName    The name of the field that was not found.
     * @param fieldValue   The value of the field that was not found.
     */
    public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue) {
        // providing customize message
        super(String.format("%s is not found with %s : %s", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

}
