package com.example.its.aifeedback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HintResponseDTO {
    private Long hintId;

    private Long questionId;

    private Long studentId;

    private String hint;

    private Integer hintCount;

    private String createdAt;
}
