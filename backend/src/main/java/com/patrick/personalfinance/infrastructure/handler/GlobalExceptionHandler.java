package com.patrick.personalfinance.infrastructure.handler;

import com.patrick.personalfinance.infrastructure.dto.ErrorResponse;
import com.patrick.personalfinance.domain.exceptions.ResourceBadRequestException;
import com.patrick.personalfinance.domain.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
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

    @ExceptionHandler(ResourceBadRequestException.class)
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        String validationErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now().toString(),
                String.valueOf(HttpStatus.BAD_REQUEST.value()),
                "Validation Failed",
                validationErrors,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
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

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handlerDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String dateTime = LocalDateTime.now().toString();

        ErrorResponse errorResponse = new ErrorResponse(
                dateTime,
                String.valueOf(HttpStatus.CONFLICT.value()),
                "Conflict",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handlerBadCredentialsException(BadCredentialsException ex) {
        String dateTime = LocalDateTime.now().toString();

        ErrorResponse errorResponse = new ErrorResponse(
                dateTime,
                String.valueOf(HttpStatus.UNAUTHORIZED.value()),
                "Unauthorized",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
}
