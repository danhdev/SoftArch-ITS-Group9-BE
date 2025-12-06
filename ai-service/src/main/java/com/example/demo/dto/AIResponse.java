package com.example.demo.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for AI operations.
 * Contains the result and optional metadata.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIResponse {

    private String result;

    private Map<String, Object> metadata;

    /**
     * Factory method for creating a simple response.
     */
    public static AIResponse of(String result) {
        return AIResponse.builder()
                .result(result)
                .build();
    }

    /**
     * Factory method for creating a response with metadata.
     */
    public static AIResponse of(String result, Map<String, Object> metadata) {
        return AIResponse.builder()
                .result(result)
                .metadata(metadata)
                .build();
    }
}
