package com.example.demo.services.feedback;

import java.util.List;
import java.util.Optional;

import com.example.demo.dto.request.AIFeedbackRequest;
import com.example.demo.dto.AIResponse;
import com.example.demo.models.FeedbackRecord;

/**
 * Service interface for feedback-related operations.
 * Follows Interface Segregation Principle (ISP) - provides only feedback-specific methods.
 */
public interface ITestFeedbackService {

    /**
     * Generate feedback for a student submission.
     *
     * @param request the AI request containing the submission
     * @return AI response with feedback
     */
    AIResponse feedback(AIFeedbackRequest request);

    /**
     * Get feedback history for a student.
     *
     * @param studentId the student's unique identifier
     * @return list of feedback records
     */
    List<FeedbackRecord> getHistory(String studentId);

    /**
     * Get feedback by student and assessment.
     *
     * @param studentId    the student's unique identifier
     * @param assessmentId the assessment's unique identifier
     * @return optional feedback record
     */
    Optional<FeedbackRecord> getByAssessment(String studentId, String assessmentId);
}
