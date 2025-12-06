package com.example.its.aifeedback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic response wrapper for API responses.
 * Provides a consistent structure for all API responses including:
 * - HTTP status code
 * - Human-readable message
 * - Generic data payload
 * 
 * @param <T> the type of data payload
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseObject<T> {

    private int status;

    private String message;

    private T data;

    /**
     * Factory method to create a successful response.
     */
    public static <T> ResponseObject<T> success(String message, T data) {
        return ResponseObject.<T>builder()
                .status(200)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Factory method to create a successful response with default message.
     */
    public static <T> ResponseObject<T> success(T data) {
        return success("Success", data);
    }

    /**
     * Factory method to create an error response.
     */
    public static <T> ResponseObject<T> error(int status, String message) {
        return ResponseObject.<T>builder()
                .status(status)
                .message(message)
                .data(null)
                .build();
    }
}
