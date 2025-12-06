package com.example.its.aifeedback.exception;

import com.example.its.aifeedback.dto.ResponseObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the AI Feedback module.
 * Provides consistent error responses across all endpoints.
 * 
 * Using @RestControllerAdvice ensures all exceptions are handled
 * uniformly and returned in the ResponseObject format.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles FeedbackNotFoundException.
     * Returns 404 Not Found with error message.
     */
    @ExceptionHandler(FeedbackNotFoundException.class)
    public ResponseEntity<ResponseObject<Void>> handleFeedbackNotFound(FeedbackNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseObject.error(404, ex.getMessage()));
    }

    /**
     * Handles validation errors from @Valid annotations.
     * Returns 400 Bad Request with field-specific error messages.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseObject<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseObject.<Map<String, String>>builder()
                        .status(400)
                        .message("Validation failed")
                        .data(errors)
                        .build());
    }

    /**
     * Handles all other unexpected exceptions.
     * Returns 500 Internal Server Error with generic message.
     * In production, you might want to log the actual error and return a generic
     * message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseObject<Void>> handleGenericException(Exception ex) {
        // Log the error (in production, use proper logging)
        ex.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseObject.error(500,
                        "An unexpected error occurred. Please try again later."));
    }
}
