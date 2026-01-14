package com.task.fileuploadapp.web.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.task.fileuploadapp.service.exception.BadRequestException;
import com.task.fileuploadapp.service.exception.ConflictException;
import com.task.fileuploadapp.service.exception.NotFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public org.springframework.http.ResponseEntity<ApiError> handleBadRequest(BadRequestException ex) {
        return org.springframework.http.ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public org.springframework.http.ResponseEntity<ApiError> handleConflict(ConflictException ex) {
        return org.springframework.http.ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public org.springframework.http.ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
        return org.springframework.http.ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public org.springframework.http.ResponseEntity<ApiError> handleUnknown(Exception ex) {
        return org.springframework.http.ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError("Unexpected error occurred."));
    }
}
