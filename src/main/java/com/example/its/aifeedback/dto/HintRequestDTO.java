package com.example.its.aifeedback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HintRequestDTO {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Question ID is required")
    private Long questionId;

    @NotBlank(message = "Question text is required")
    private String questionText;

    @NotBlank(message = "Correct answer is required")
    private String correctAnswer;

    private String subject;

    private String topic;

    private String difficulty;

    private Integer hintLimit;
}
