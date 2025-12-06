package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for material recommendation operations.
 * Contains identifiers for student, course, and optional preferences.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIMaterialRequest {

    @NotNull(message = "Student ID is required")
    private String studentId;

    @NotNull(message = "Course ID is required")
    private String courseId;

    /**
     * Student's prompt or query for material recommendations.
     */
    private String studentPrompt;

    /**
     * Optional difficulty level preference (EASY, MEDIUM, HARD).
     */
    private String preferredDifficulty;

    /**
     * Optional material type preference (TEXT, VIDEO, INTERACTIVE).
     */
    private String preferredType;
}
