package com.example.springboot_wordle.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNotFound(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorBody(404, "Not Found", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorBody(400, "Bad Request", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleServerError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorBody(500, "Internal Server Error", ex.getMessage()));
    }

    private Map<String, Object> errorBody(int status, String error, String message) {
        return Map.of(
                "timestamp", Instant.now().toString(),
                "status", status,
                "error", error,
                "message", message
        );
    }
}
