package com.example.demo.services.hint;

import org.springframework.stereotype.Service;

import com.example.demo.dto.request.AIHintRequest;
import com.example.demo.dto.AIResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of HintService.
 * Follows Single Responsibility Principle - handles only hint generation operations.
 * Uses Strategy Pattern via AI Tasks.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HintServiceImpl implements HintService {

    private final HintGenerationTask hintGenerationTask;

    @Override
    public AIResponse hint(AIHintRequest request) {
        log.info("Processing hint generation request for course: {}, assessment: {}, question: {}",
                request.getCourseId(), request.getAssessmentId(), request.getQuestionId());

        System.out.println("HintServiceImpl: Delegating to HintGenerationTask");

        return hintGenerationTask.execute(request);
    }
}
