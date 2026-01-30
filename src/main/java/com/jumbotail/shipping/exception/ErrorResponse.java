package com.jumbotail.shipping.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Standardized error response structure for all API errors.
 * Follows RFC 7807 Problem Details for HTTP APIs pattern.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private int status;

    private String error;

    private String message;

    private String path;

    /**
     * Detailed field-level validation errors.
     * Key: field name, Value: error message
     */
    private Map<String, String> fieldErrors;

    /**
     * List of validation error messages (for constraint violations).
     */
    private List<String> errors;

    /**
     * Unique trace ID for debugging purposes.
     */
    private String traceId;
}
