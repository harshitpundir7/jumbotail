package com.jumbotail.shipping.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a request contains invalid data or parameters.
 */
public class InvalidRequestException extends RuntimeException {

    private final String field;
    private final Object rejectedValue;

    public InvalidRequestException(String message) {
        super(message);
        this.field = null;
        this.rejectedValue = null;
    }

    public InvalidRequestException(String field, Object rejectedValue, String message) {
        super(message);
        this.field = field;
        this.rejectedValue = rejectedValue;
    }

    public String getField() {
        return field;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }

    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
