package com.example.demo.services.hint;

import org.springframework.stereotype.Service;

import com.example.demo.dto.AIResponse;
import com.example.demo.dto.request.AIHintRequest;
import com.example.demo.models.AIHint;
import com.example.demo.services.dataprovider.HintDataProvider;
import com.example.demo.services.task.AITask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Implementation of HintService.
 * Follows Single Responsibility Principle - orchestrates hint generation operations.
 * Follows Dependency Inversion Principle - depends on AITask abstraction, not concrete class.
 * Uses Strategy Pattern via AI Tasks.
 * Delegates all business logic to HintGenerationTask.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HintServiceImpl implements HintService {

    private final AITask<AIHintRequest> hintGenerationTask;
    private final HintDataProvider hintDataProvider;

    @Override
    public AIResponse hint(AIHintRequest request) {
        log.info("Processing hint generation request for student: {}, course: {}, assessment: {}, question: {}",
                request.getStudentId(), request.getCourseId(), request.getAssessmentId(), request.getQuestionId());

        // Delegate to AI Task which handles all data gathering and hint generation
        AIResponse response = hintGenerationTask.execute(request);

        log.info("Hint generation completed for student: {}, question: {}",
                request.getStudentId(), request.getQuestionId());

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
