package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for AI explanation history.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIExplainResponseDTO {
    private Long explanationId;
    private Long studentId;
    private Long materialId;
    private String studentQuestion;
    private String explanation;
    private String createdAt;
}

