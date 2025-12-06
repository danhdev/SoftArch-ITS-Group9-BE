package com.example.its.aifeedback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for receiving AI feedback generation requests.
 * Contains all necessary information about a student's submission
 * to generate intelligent feedback.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIFeedbackRequestDTO {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Question ID is required")
    private Long questionId;

    @NotBlank(message = "Question text is required")
    private String questionText;

    @NotBlank(message = "Student answer is required")
    private String studentAnswer;

    @NotBlank(message = "Correct answer is required")
    private String correctAnswer;

    private String topic;

    private String difficulty;

    private String subject;
}
