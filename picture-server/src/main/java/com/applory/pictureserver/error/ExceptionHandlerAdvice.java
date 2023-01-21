package com.applory.pictureserver.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiError handleMethodArgumentNotValidException (MethodArgumentNotValidException exception, HttpServletRequest request) {
        log.error(exception.getMessage());
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), "Validation error", request.getServletPath());

        BindingResult bindingResult = exception.getBindingResult();

        Map<String, String> validationErrors = new HashMap<>();

        for (FieldError fieldError: bindingResult.getFieldErrors()) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        apiError.setValidationErrors(validationErrors);
        return apiError;
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiError handleRequestBodyValidationException (BindException exception, HttpServletRequest request) {
        log.error(exception.getMessage());
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), "Validation error", request.getServletPath());

        BindingResult bindingResult = exception.getBindingResult();

        Map<String, String> validationErrors = new HashMap<>();

        for (FieldError fieldError: bindingResult.getFieldErrors()) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        apiError.setValidationErrors(validationErrors);
        return apiError;
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiError handleIllegalStateException (IllegalStateException exception, HttpServletRequest request) {
        log.error(exception.getMessage());

        return new ApiError(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), request.getServletPath());
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ApiError handleNoSuchElementException (NoSuchElementException exception, HttpServletRequest request) {
        log.error(exception.getMessage());

        return new ApiError(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), request.getServletPath());
    }
}
