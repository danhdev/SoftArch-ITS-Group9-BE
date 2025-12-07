package com.example.demo.services.dataprovider;

import com.example.demo.models.FeedbackRecord;

import java.util.List;
import java.util.Optional;

/**
 * Interface for feedback data persistence operations.
 * Follows Interface Segregation Principle (ISP) - provides only feedback-specific data operations.
 * Follows Dependency Inversion Principle (DIP) - high-level modules depend on this abstraction.
 */
public interface FeedbackDataProvider {

    /**
     * Save a feedback record to the database.
     *
     * @param studentId    the student identifier
     * @param courseId     the course identifier
     * @param assessmentId the assessment identifier
     * @param feedbackText the generated feedback text
     * @return the saved feedback record or null if save failed
     */
    FeedbackRecord saveFeedback(String studentId, String courseId, String assessmentId, String feedbackText);

    /**
     * Get feedback history for a student.
     *
     * @param studentId the student identifier
     * @return list of feedback records
     */
    List<FeedbackRecord> getFeedbackHistory(String studentId);

    /**
     * Get feedback by student and assessment.
     *
     * @param studentId    the student identifier
     * @param assessmentId the assessment identifier
     * @return the feedback record if found
     */
    Optional<FeedbackRecord> getFeedbackByAssessment(String studentId, String assessmentId);
}
