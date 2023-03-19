package com.example.testing.controller;

import com.example.testing.exceptions.ResourceAlreadyExistException;
import com.example.testing.exceptions.ResourceNotFoundException;
import com.example.testing.payload.ApiErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorDto> handleAuthenticationException(BadCredentialsException e) {
        log.error("Handle bad credentials exception: {}", e.getMessage(), e);

        String error = "Invalid username or password";
        return buildResponseEntity(new ApiErrorDto(UNAUTHORIZED, error, e));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ApiErrorDto> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.error("Handle message not readable exception: {}", e.getMessage(), e);

        String error = "Malformed JSON request";
        return buildResponseEntity(new ApiErrorDto(BAD_REQUEST, error, e));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<ApiErrorDto> handleResourceNotFound(ResourceNotFoundException e) {
        log.error("Handle message not readable exception: {}", e.getMessage(), e);

        return buildResponseEntity(new ApiErrorDto(NOT_FOUND, e.getMessage(), e));
    }

    @ExceptionHandler(ResourceAlreadyExistException.class)
    protected ResponseEntity<ApiErrorDto> handleResourceAlreadyExistsException(ResourceAlreadyExistException e) {
        log.error("Handle resource already exist exception: {}", e.getMessage(), e);

        return buildResponseEntity(new ApiErrorDto(CONFLICT, e.getMessage(), e));
    }

    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ApiErrorDto> handleMethodArgumentNotValid(BindException e) {
        log.error("handle bind exception: {}", e.getMessage(), e);

        Optional<FieldError> fieldError = e.getFieldErrors().stream().findFirst();
        String message = fieldError.isEmpty() ? "Invalid input" : fieldError.get().getDefaultMessage();

        ApiErrorDto apiError = new ApiErrorDto(BAD_REQUEST, message);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(IllegalStateException.class)
    protected ResponseEntity<ApiErrorDto> handleIllegalStateException(IllegalStateException e) {
        log.error("handle illegal state exception: {}", e.getMessage(), e);

        return buildResponseEntity(new ApiErrorDto(BAD_REQUEST, e.getMessage(), e));
    }

    private ResponseEntity<ApiErrorDto> buildResponseEntity(ApiErrorDto apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
