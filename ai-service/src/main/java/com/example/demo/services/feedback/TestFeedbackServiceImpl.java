package com.example.demo.services.feedback;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.AIResponse;
import com.example.demo.dto.request.AIFeedbackRequest;
import com.example.demo.models.FeedbackRecord;
import com.example.demo.services.dataprovider.FeedbackDataProvider;
import com.example.demo.services.task.AITask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of FeedbackService.
 * Follows Single Responsibility Principle - orchestrates feedback generation operations.
 * Follows Dependency Inversion Principle - depends on AITask abstraction, not concrete class.
 * Uses Strategy Pattern via AI Tasks.
 * Delegates all business logic to TestFeedbackGenerationTask.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TestFeedbackServiceImpl implements ITestFeedbackService {

    private final AITask<AIFeedbackRequest> testFeedbackGenerationTask;
    private final FeedbackDataProvider feedbackDataProvider;

    @Override
    @Transactional
    public AIResponse feedback(AIFeedbackRequest request) {
        log.info("Processing feedback generation request for student: {}, assessment: {}",
                request.getStudentId(), request.getAssessmentId());

        // Delegate to AI Task which handles all data gathering and feedback generation
        AIResponse response = testFeedbackGenerationTask.execute(request);

        log.info("Feedback generation completed for student: {}, assessment: {}",
                request.getStudentId(), request.getAssessmentId());

        return response;
    }

    @Override
    public List<FeedbackRecord> getHistory(String studentId) {
        log.info("Retrieving feedback history for student: {}", studentId);
        // Delegate to FeedbackDataProvider (SRP compliance)
        return feedbackDataProvider.getFeedbackHistory(studentId);
    }

    @Override
    public Optional<FeedbackRecord> getByAssessment(String studentId, String assessmentId) {
        log.info("Retrieving feedback for student: {}, assessment: {}", studentId, assessmentId);
        // Delegate to FeedbackDataProvider (SRP compliance)
        return feedbackDataProvider.getFeedbackByAssessment(studentId, assessmentId);
    }
}
