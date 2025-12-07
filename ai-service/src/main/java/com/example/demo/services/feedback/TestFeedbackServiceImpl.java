package com.example.demo.services.feedback;

import java.util.List;
import java.util.Optional;

import com.example.demo.dto.TestResponseDTO;
import com.example.demo.proxy.TestProxyClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.request.AIFeedbackRequest;
import com.example.demo.dto.AIResponse;
import com.example.demo.models.FeedbackRecord;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of FeedbackService.
 * Follows Single Responsibility Principle - handles only feedback generation operations.
 * Uses composition to delegate to specialized services (FeedbackHistoryService, StudentProfileService).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TestFeedbackServiceImpl implements ITestFeedbackService {

    private final TestFeedbackGenerationTask testFeedbackGenerationTask;
    private final ITestFeedbackHistoryService feedbackHistoryService;
    private final TestProxyClient testProxyClient;

    @Override
    @Transactional
    public AIResponse feedback(AIFeedbackRequest request) {
        log.info("Processing feedback generation request for student: {}, assessment: {}",
                request.getStudentId(), request.getAssessmentId());

        // Fetch test context from external service
        TestResponseDTO testContext = testProxyClient.getTestByStudentAndCourse(
                request.getCourseId(),
                request.getAssessmentId(),
                request.getStudentId()
        );
        log.info("Fetched test context: {} - {}", testContext.getTitle(), testContext.getDescription());

        // Generate feedback using AI task with test context
        AIResponse response = testFeedbackGenerationTask.execute(request, testContext);

        // Save feedback record
        FeedbackRecord record = FeedbackRecord.builder()
                .studentId(request.getStudentId())
                .courseId(request.getCourseId())
                .assessmentId(request.getAssessmentId())
                .feedbackText(response.getResult())
                .build();

        feedbackHistoryService.save(record);

        return response;
    }

    @Override
    public List<FeedbackRecord> getHistory(String studentId) {
        log.info("Retrieving feedback history for student: {}", studentId);
        return feedbackHistoryService.getHistory(studentId);
    }

    @Override
    public Optional<FeedbackRecord> getByAssessment(String studentId, String assessmentId) {
        log.info("Retrieving feedback for student: {}, assessment: {}", studentId, assessmentId);
        return feedbackHistoryService.getByAssessment(studentId, assessmentId);
    }
}
