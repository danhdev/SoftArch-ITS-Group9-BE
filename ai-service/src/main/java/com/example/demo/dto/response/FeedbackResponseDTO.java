package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for feedback record.
 * Used by FeedbackController to return feedback history and assessment feedback.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackResponseDTO {
    
    private Long id;
    
    private String studentId;
    
    private String courseId;
    
    private String assessmentId;
    
    private String feedbackText;
    
    private String createdAt;
}
