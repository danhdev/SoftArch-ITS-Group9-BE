package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Request DTO for hint generation operations.
 * Contains identifiers for course, assessment, and specific question.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIHintRequest {

    @NotNull(message = "Course ID is required")
    private String courseId;

    @NotNull(message = "Assessment ID is required")
    private String assessmentId;

    @NotNull(message = "Question ID is required")
    private Long questionId;
}
