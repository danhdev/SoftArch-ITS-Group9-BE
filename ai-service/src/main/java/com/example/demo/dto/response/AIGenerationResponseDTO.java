package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Generic response DTO for AI generation results.
 * Used by all controllers for generate/recommend/explain endpoints.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIGenerationResponseDTO {
    
    /**
     * The generated AI result text.
     */
    private String result;
    
    /**
     * Optional metadata about the generation.
     */
    private Map<String, Object> metadata;
    
    /**
     * Factory method for creating a simple response.
     */
    public static AIGenerationResponseDTO of(String result) {
        return AIGenerationResponseDTO.builder()
                .result(result)
                .build();
    }
    
    /**
     * Factory method for creating a response with metadata.
     */
    public static AIGenerationResponseDTO of(String result, Map<String, Object> metadata) {
        return AIGenerationResponseDTO.builder()
                .result(result)
                .metadata(metadata)
                .build();
    }
}
