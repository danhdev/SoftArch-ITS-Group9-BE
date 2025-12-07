package com.example.demo.services.feedback;

import java.util.List;
import java.util.Optional;

import com.example.demo.dto.TestResponseDTO;
import com.example.demo.services.dataprovider.FeedbackDataProvider;
import com.example.demo.services.dataprovider.TestDataProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.request.AIFeedbackRequest;
import com.example.demo.dto.AIResponse;
import com.example.demo.models.FeedbackRecord;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of FeedbackService.
 * Follows Single Responsibility Principle - orchestrates feedback generation operations.
 * Uses composition to delegate to specialized services.
 * Delegates data fetching to DataProviders (SRP compliance).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TestFeedbackServiceImpl implements ITestFeedbackService {

    private final TestFeedbackGenerationTask testFeedbackGenerationTask;
    
    // DataProviders for SOLID compliance - separate data access concerns
    private final TestDataProvider testDataProvider;
    private final FeedbackDataProvider feedbackDataProvider;

    @Override
    @Transactional
    public AIResponse feedback(AIFeedbackRequest request) {
        log.info("Processing feedback generation request for student: {}, assessment: {}",
                request.getStudentId(), request.getAssessmentId());

        // Delegate test context fetching to TestDataProvider (SRP compliance)
        TestResponseDTO testContext = testDataProvider.getTestContextForStudent(
                request.getCourseId(),
                request.getAssessmentId(),
                request.getStudentId()
        );
        
        if (testContext != null) {
            log.info("Fetched test context: {} - {}", testContext.getTitle(), testContext.getDescription());
        }

        // Generate feedback using AI task with test context
        AIResponse response = testFeedbackGenerationTask.execute(request, testContext);

        // Delegate saving to FeedbackDataProvider (SRP compliance)
        feedbackDataProvider.saveFeedback(
                request.getStudentId(),
                request.getCourseId(),
                request.getAssessmentId(),
                response.getResult()
        );

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
