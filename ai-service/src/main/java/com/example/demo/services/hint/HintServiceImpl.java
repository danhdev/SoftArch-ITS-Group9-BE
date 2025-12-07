package com.example.demo.services.hint;

import org.springframework.stereotype.Service;

import com.example.demo.dto.*;
import com.example.demo.dto.request.AIHintRequest;
import com.example.demo.models.AIHint;
import com.example.demo.services.dataprovider.CourseDataProvider;
import com.example.demo.services.dataprovider.HintDataProvider;
import com.example.demo.services.dataprovider.TestDataProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Implementation of HintService.
 * Follows Single Responsibility Principle - orchestrates hint generation operations.
 * Uses Strategy Pattern via AI Tasks.
 * Delegates data fetching to DataProviders (SRP compliance).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HintServiceImpl implements HintService {

    private final HintGenerationTask hintGenerationTask;
    
    // DataProviders for SOLID compliance - separate data access concerns
    private final HintDataProvider hintDataProvider;
    private final TestDataProvider testDataProvider;
    private final CourseDataProvider courseDataProvider;

    @Override
    public AIResponse hint(AIHintRequest request) {
        log.info("Processing hint generation request for student: {}, course: {}, assessment: {}, question: {}",
                request.getStudentId(), request.getCourseId(), request.getAssessmentId(), request.getQuestionId());

        // Delegate data fetching to DataProviders (SRP compliance)
        List<String> previousHints = hintDataProvider.getPreviousHintTexts(request.getStudentId(), request.getQuestionId());

        // Fetch test context from TestDataProvider
        TestResponseDTO testContext = testDataProvider.getTestContext(request.getCourseId(), request.getAssessmentId());

        // Find the specific question (returns Optional, use orElse for null fallback)
        QuestionDTO targetQuestion = testDataProvider.findQuestion(testContext, request.getQuestionId()).orElse(null);

        // Fetch subject (course name) from CourseDataProvider
        String subject = courseDataProvider.getCourseName(request.getCourseId());

        // Fetch course materials from CourseDataProvider
        List<MaterialDTO> materials = courseDataProvider.getCourseMaterials(request.getCourseId());

        // Execute the hint generation task with all context
        AIResponse response = hintGenerationTask.execute(
                request,
                previousHints,
                testContext,
                targetQuestion,
                subject,
                materials
        );

        // Delegate saving to HintDataProvider (SRP compliance)
        if (response != null && response.getResult() != null) {
            hintDataProvider.saveHint(request.getStudentId(), request.getQuestionId(), response.getResult());
        }

        return response;
    }

    @Override
    public List<AIHint> getHintHistory(Long studentId, Long questionId) {
        log.info("Fetching hint history for student: {}, question: {}", studentId, questionId);

        // Delegate data fetching to HintDataProvider (SRP compliance)
        List<AIHint> hints = hintDataProvider.getHintHistory(studentId, questionId);

        log.info("Found {} hints for student: {}, question: {}", hints.size(), studentId, questionId);

        return hints;
    }
}
