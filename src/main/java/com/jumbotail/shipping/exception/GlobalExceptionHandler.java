package com.jumbotail.shipping.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Global exception handler that provides consistent error responses across all
 * API endpoints.
 * Implements centralized exception handling using @RestControllerAdvice.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        /**
         * Handles ResourceNotFoundException (404 Not Found).
         */
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
                        ResourceNotFoundException ex, HttpServletRequest request) {

                String traceId = generateTraceId();
                log.warn("Resource not found [traceId={}]: {}", traceId, ex.getMessage());

                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND.value())
                                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .traceId(traceId)
                                .build();

                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        /**
         * Handles InvalidRequestException (400 Bad Request).
         */
        @ExceptionHandler(InvalidRequestException.class)
        public ResponseEntity<ErrorResponse> handleInvalidRequestException(
                        InvalidRequestException ex, HttpServletRequest request) {

                String traceId = generateTraceId();
                log.warn("Invalid request [traceId={}]: {}", traceId, ex.getMessage());

                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .traceId(traceId)
                                .build();

                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        /**
         * Handles validation errors from @Valid annotated request bodies.
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
                        MethodArgumentNotValidException ex, HttpServletRequest request) {

                String traceId = generateTraceId();
                Map<String, String> fieldErrors = new HashMap<>();

                ex.getBindingResult().getAllErrors().forEach(error -> {
                        String fieldName = ((FieldError) error).getField();
                        String errorMessage = error.getDefaultMessage();
                        fieldErrors.put(fieldName, errorMessage);
                });

                log.warn("Validation failed [traceId={}]: {}", traceId, fieldErrors);

                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .message("Validation failed. Please check the field errors.")
                                .path(request.getRequestURI())
                                .fieldErrors(fieldErrors)
                                .traceId(traceId)
                                .build();

                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        /**
         * Handles constraint violation exceptions from @Validated annotated parameters.
         */
        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ErrorResponse> handleConstraintViolationException(
                        ConstraintViolationException ex, HttpServletRequest request) {

                String traceId = generateTraceId();
                List<String> errors = ex.getConstraintViolations().stream()
                                .map(ConstraintViolation::getMessage)
                                .collect(Collectors.toList());

                log.warn("Constraint violation [traceId={}]: {}", traceId, errors);

                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .message("Constraint violation. Please check the errors.")
                                .path(request.getRequestURI())
                                .errors(errors)
                                .traceId(traceId)
                                .build();

                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        /**
         * Handles missing required request parameters.
         */
        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
                        MissingServletRequestParameterException ex, HttpServletRequest request) {

                String traceId = generateTraceId();
                String message = String.format("Required parameter '%s' of type %s is missing",
                                ex.getParameterName(), ex.getParameterType());

                log.warn("Missing parameter [traceId={}]: {}", traceId, message);

                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .message(message)
                                .path(request.getRequestURI())
                                .traceId(traceId)
                                .build();

                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        /**
         * Handles type mismatch in request parameters.
         */
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
                        MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

                String traceId = generateTraceId();
                String message = String.format("Parameter '%s' should be of type %s",
                                ex.getName(),
                                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

                log.warn("Type mismatch [traceId={}]: {}", traceId, message);

                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .message(message)
                                .path(request.getRequestURI())
                                .traceId(traceId)
                                .build();

                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        /**
         * Handles malformed JSON in request body.
         */
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
                        HttpMessageNotReadableException ex, HttpServletRequest request) {

                String traceId = generateTraceId();
                log.warn("Malformed request body [traceId={}]: {}", traceId, ex.getMessage());

                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .message("Malformed JSON request body. Please check the request format.")
                                .path(request.getRequestURI())
                                .traceId(traceId)
                                .build();

                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        /**
         * Handles IllegalArgumentException for invalid enum values and business logic
         * errors.
         */
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
                        IllegalArgumentException ex, HttpServletRequest request) {

                String traceId = generateTraceId();
                log.warn("Invalid argument [traceId={}]: {}", traceId, ex.getMessage());

                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .traceId(traceId)
                                .build();

                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        /**
         * Handles all other unhandled exceptions (500 Internal Server Error).
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGenericException(
                        Exception ex, HttpServletRequest request) {

                String traceId = generateTraceId();
                log.error("Unexpected error [traceId={}]: {}", traceId, ex.getMessage(), ex);

                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                                .message("An unexpected error occurred. Please contact support with trace ID: "
                                                + traceId)
                                .path(request.getRequestURI())
                                .traceId(traceId)
                                .build();

                return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        /**
         * Generates a unique trace ID for debugging purposes.
         */
        private String generateTraceId() {
                return UUID.randomUUID().toString().substring(0, 8);
        }
}
