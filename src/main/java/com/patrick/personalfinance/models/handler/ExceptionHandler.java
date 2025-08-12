package com.patrick.personalfinance.models.handler;

import com.patrick.personalfinance.models.entity.ErrorResponse;
import com.patrick.personalfinance.models.exceptions.ResourceBadRequestException;
import com.patrick.personalfinance.models.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.time.LocalDateTime;

@ControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlerResourceNotFoundException(ResourceNotFoundException ex) {

        String DataTime = LocalDateTime.now().toString();

        ErrorResponse errorResponse = new ErrorResponse(
                DataTime,
                String.valueOf(HttpStatus.NOT_FOUND.value()),
                "Not Found",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ResourceBadRequestException.class)
    public ResponseEntity<ErrorResponse> handlerResourceBadRequestException(ResourceBadRequestException ex) {

        String DataTime = LocalDateTime.now().toString();

        ErrorResponse errorResponse = new ErrorResponse(
                DataTime,
                String.valueOf(HttpStatus.BAD_REQUEST.value()),
                "Bad Request",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handlerRequestException(Exception ex) {

        String DataTime = LocalDateTime.now().toString();

        ErrorResponse errorResponse = new ErrorResponse(
                DataTime,
                String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                "Internal Server Error",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
