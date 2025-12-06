package com.example.its.aifeedback.exception;

/**
 * Exception thrown when requested feedback is not found.
 * Used to provide meaningful error responses when querying
 * for non-existent feedback records.
 */
public class FeedbackNotFoundException extends RuntimeException {

    public FeedbackNotFoundException(String message) {
        super(message);
    }

    public FeedbackNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
