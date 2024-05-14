package com.weighbridge.qualityuser.exception;

import com.weighbridge.admin.exceptions.ResourceCreationException;
import com.weighbridge.admin.exceptions.ResourceNotFoundException;
import com.weighbridge.admin.exceptions.SessionExpiredException;
import com.weighbridge.admin.payloads.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

/**
 * A global exception handler class to handle different types of exceptions.
 * This class is annotated with @RestControllerAdvice to enable global exception handling.
 */
//@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles ResourceNotFoundException.
     *
     * @param resourceNotFoundException The ResourceNotFoundException that occurred.
     * @return a ResponseEntity with a custom error message API response and HTTP status code NOT_FOUND (404).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException resourceNotFoundException) {
        // set the error message
        String message = resourceNotFoundException.getMessage();

        // setting the response
        ApiResponse response = ApiResponse.builder()
                .message(message)
                .status(HttpStatus.NOT_FOUND)
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles MethodArgumentExceptionNotFoundException.
     *
     * @param methodArgumentNotValidException The MethodArgumentNotValidException that occurred
     * @return a ResponseEntity with a custom error message API response and HTTP status code BAD_REQUEST (400).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
        Map<String, String> response = new HashMap<>();
        methodArgumentNotValidException.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            response.put(fieldName, message);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle ResourceCreationException.
     *
     * @param resourceCreationException The ResourceCreationException that occurred.
     * @return a ResponseEntity with a custom ApiResponse and status code BAD_REQUEST (400).
     */
    @ExceptionHandler(ResourceCreationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse> handleResourceCreationException(ResourceCreationException resourceCreationException) {
        String message = resourceCreationException.getMessage();

        ApiResponse response = ApiResponse.builder()
                .message(message)
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles ResourceCreationException.
     *
     * @param responseStatusException The ResponseStatusException that occurred.
     * @return a ResponseEntity with a custom ApiResponse and status code BAD_REQUEST (400).
     */
    @ExceptionHandler(ResponseStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse> handleResponseStatusException(ResponseStatusException responseStatusException) {
        String message = responseStatusException.getReason();

        ApiResponse response = ApiResponse.builder()
                .message(message)
                .status(HttpStatus.BAD_REQUEST)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles SessionExpiredException.
     *
     * @param sessionExpiredException The SessionExpiredException that occurred.
     * @return a ResponseEntity with a custom ApiResponse and a status code BAD_REQUEST (400).
     */
    @ExceptionHandler(SessionExpiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse> handleSessionExpiredException(SessionExpiredException sessionExpiredException) {
        String message = sessionExpiredException.getMessage();

        ApiResponse response = ApiResponse.builder()
                .message(message)
                .status(HttpStatus.BAD_REQUEST)
                .build();
        return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }
}
