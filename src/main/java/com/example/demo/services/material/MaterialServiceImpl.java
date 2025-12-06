package com.example.demo.services.material;

import org.springframework.stereotype.Service;

import com.example.demo.dto.request.AIFeedbackRequest;
import com.example.demo.dto.AIResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of MaterialService.
 * Follows Single Responsibility Principle - handles only material-related operations.
 * Uses Strategy Pattern via AI Tasks for different operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MaterialServiceImpl implements IMaterialService {

    private final MaterialRecommendationTask recommendationTask;
    private final MaterialExplanationTask explanationTask;

    @Override
    public AIResponse recommend(AIFeedbackRequest request) {
        log.info("Processing material recommendation request for student: {}", request.getStudentId());
        return recommendationTask.execute(request);
    }

    @Override
    public AIResponse explain(AIFeedbackRequest request) {
        log.info("Processing material explanation request");
        return explanationTask.execute(request);
    }
}
