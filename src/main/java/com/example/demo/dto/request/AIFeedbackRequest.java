package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Request DTO for AI operations.
 * Contains the type of request and student/course/assessment identifiers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIFeedbackRequest {

//    @NotBlank(message = "Request type is required")
//    private String type;

    @NotNull
    private String studentId;

    @NotNull
    private String courseId;

    @NotNull
    private String assessmentId;
}
