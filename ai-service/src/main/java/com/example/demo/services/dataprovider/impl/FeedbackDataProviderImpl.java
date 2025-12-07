package com.example.demo.services.dataprovider.impl;

import com.example.demo.models.FeedbackRecord;
import com.example.demo.services.dataprovider.FeedbackDataProvider;
import com.example.demo.services.feedback.ITestFeedbackHistoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of FeedbackDataProvider.
 * Follows Single Responsibility Principle - handles only feedback data persistence operations.
 * Centralizes all feedback-related database operations.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FeedbackDataProviderImpl implements FeedbackDataProvider {

    private final ITestFeedbackHistoryService feedbackHistoryService;

    @Override
    public FeedbackRecord saveFeedback(String studentId, String courseId, String assessmentId, String feedbackText) {
        try {
            if (studentId == null || assessmentId == null || feedbackText == null) {
                log.warn("Cannot save feedback: missing required fields (studentId: {}, assessmentId: {}, feedbackText: {})",
                        studentId, assessmentId, feedbackText != null ? "present" : "null");
                return null;
            }

            FeedbackRecord record = FeedbackRecord.builder()
                    .studentId(studentId)
                    .courseId(courseId)
                    .assessmentId(assessmentId)
                    .feedbackText(feedbackText)
                    .build();

            FeedbackRecord savedRecord = feedbackHistoryService.save(record);
            log.info("Saved feedback to database for student: {}, assessment: {}", studentId, assessmentId);
            return savedRecord;
        } catch (Exception e) {
            log.error("Failed to save feedback to database for student: {}, assessment: {}. Error: {}",
                    studentId, assessmentId, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<FeedbackRecord> getFeedbackHistory(String studentId) {
        try {
            if (studentId == null) {
                return new ArrayList<>();
            }
            List<FeedbackRecord> records = feedbackHistoryService.getHistory(studentId);
            log.info("Fetched {} feedback records for student: {}", records.size(), studentId);
            return records;
        } catch (Exception e) {
            log.warn("Failed to fetch feedback history for student: {}. Error: {}", studentId, e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Optional<FeedbackRecord> getFeedbackByAssessment(String studentId, String assessmentId) {
        try {
            if (studentId == null || assessmentId == null) {
                return Optional.empty();
            }
            Optional<FeedbackRecord> record = feedbackHistoryService.getByAssessment(studentId, assessmentId);
            log.info("Fetched feedback for student: {}, assessment: {}, found: {}", 
                    studentId, assessmentId, record.isPresent());
            return record;
        } catch (Exception e) {
            log.warn("Failed to fetch feedback for student: {}, assessment: {}. Error: {}", 
                    studentId, assessmentId, e.getMessage());
            return Optional.empty();
        }
    }
}
