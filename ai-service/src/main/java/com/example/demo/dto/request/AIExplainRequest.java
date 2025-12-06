package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIExplainRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Material ID is required")
    private Long materialId;

    @NotNull(message = "Student question text is required")
    private String studentQuestion;
}
