package com.patrick.personalfinance.models.exceptions;

public class ResourceBadRequestException extends RuntimeException {

    public ResourceBadRequestException (String message) {
        super(message);
    }
}
