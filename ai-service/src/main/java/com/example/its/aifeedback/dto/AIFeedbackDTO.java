package com.example.its.aifeedback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for AI feedback response data.
 * Used to transfer feedback information from service layer to client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIFeedbackDTO {

    private Long id;

    private Long studentId;

    private Long questionId;

    private String feedbackText;

    private String hint;
}
