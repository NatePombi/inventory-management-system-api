package com.nate.inventorymanagementsystemapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.rmi.AccessException;
import java.time.Instant;
@RestController
public class GlobalExceptionHandler {
    record ApiError(Instant timestamp, int status,String error, String message, String path){}

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleProductNotFound(ProductNotFoundException ex, jakarta.servlet.http.HttpServletRequest req){
        return new ApiError(Instant.now(),404,"Not Found",ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleUserNotFound(UserNotFoundException ex, jakarta.servlet.http.HttpServletRequest req){
        return new ApiError(Instant.now(),404,"Not Found", ex.getMessage(),req.getRequestURI());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleAccessDenied(AccessDeniedException ex, jakarta.servlet.http.HttpServletRequest req){
        return new ApiError(Instant.now(),403,"Forbidden",ex.getMessage(),req.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(MethodArgumentNotValidException ex, jakarta.servlet.http.HttpServletRequest req){
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e-> e.getField() + ": " + e.getDefaultMessage())
                .findFirst()
                .orElse("Failed Validation");

        return new ApiError(Instant.now(),400,"Bad Request",msg, req.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(Exception ex, jakarta.servlet.http.HttpServletRequest req){
        return new ApiError(Instant.now(),500, "Internal Server Error", ex.getMessage(), req.getRequestURI());
    }
}
