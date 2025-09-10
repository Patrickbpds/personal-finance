package com.patrick.personalfinance.domain.exceptions;

public class ResourceBadRequestException extends RuntimeException {

    public ResourceBadRequestException (String message) {
        super(message);
    }
}
