package com.weighbridge.qualityuser.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * This class represents the response structure when an exception is thrown during request processing.
 * It contains a message and a status code.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse {

    /**
     * The error message.
     */
    private String message;

    /**
     * The HTTP status code to be returned.
     */
    private HttpStatus status;
}
