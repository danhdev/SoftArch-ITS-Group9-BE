package com.example.demo.services.feedback;

import java.util.List;
import java.util.Optional;

import com.example.demo.models.FeedbackRecord;

/**
 * Service interface for feedback history operations.
 * Follows Interface Segregation Principle (ISP) - provides only history-specific methods.
 */
public interface ITestFeedbackHistoryService {

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

    /**
     * Save a feedback record.
     *
     * @param record the feedback record to save
     * @return the saved feedback record
     */
    FeedbackRecord save(FeedbackRecord record);
}
