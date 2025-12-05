package com.example.its.aifeedback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIExplainResponseDTO {
    private Long explainId;

    private Long studentId;

    private Long materialId;

    private String explanation;

    private String createdAt;
}
