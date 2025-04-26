package com.hurricanepilot.hmctsdt.api.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hurricanepilot.hmctsdt.api.model.Error;
import com.hurricanepilot.hmctsdt.service.exception.TaskNotFoundException;
import com.hurricanepilot.hmctsdt.service.exception.TaskStatusInvalidException;
import com.hurricanepilot.hmctsdt.service.exception.TaskUpdateNotSupportedException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(exception = TaskNotFoundException.class)
    public ResponseEntity<Error> handleTaskNotFound(TaskNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler(exception = TaskStatusInvalidException.class)
    public ResponseEntity<Error> handleInvalidStatus(TaskStatusInvalidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(exception = TaskUpdateNotSupportedException.class)
    public ResponseEntity<Error> handleInvalidStatus(TaskUpdateNotSupportedException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(exception = MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleValidationError(MethodArgumentNotValidException e) {

        var builder = new StringBuilder("Validation errors are present:\n");

        e.getBindingResult().getAllErrors().forEach(err -> {
            var field = err.getObjectName();
            if (err instanceof FieldError fieldError) {
                field = fieldError.getField();
            }
            builder.append(String.format("%s: %s%n", field, err.getDefaultMessage()));
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Error(HttpStatus.BAD_REQUEST.value(), builder.toString()));
    }

    @ExceptionHandler(exception = Exception.class)
    public ResponseEntity<Error> handleError(Exception e) {

        var status = HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value());
        var message = "Internal Server Error";

        // decant any existing detail message into our own error
        // response to keep things consistent
        if(e instanceof ErrorResponse er) {
            status = er.getStatusCode();
            message = er.getBody().getDetail();
        }

        return ResponseEntity.status(status)
                .body(new Error(status.value(), message));
    }

}
