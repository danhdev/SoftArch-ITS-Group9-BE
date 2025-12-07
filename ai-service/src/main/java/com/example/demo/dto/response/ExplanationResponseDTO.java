package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for AI explanation history.
 * Used by MaterialController to return explanation results and history.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExplanationResponseDTO {
    
    private Long explanationId;
    
    private Long studentId;
    
    private Long materialId;
    
    private String studentQuestion;
    
    private String explanation;
    
    private String createdAt;
}
