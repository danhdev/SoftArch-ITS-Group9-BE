package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for hint information.
 * Contains the hint details including the generated hint text and metadata.
 */
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

