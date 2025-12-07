package com.example.demo.services.material;

import java.util.List;

import com.example.demo.dto.request.AIExplainRequest;
import com.example.demo.dto.request.AIMaterialRequest;
import com.example.demo.dto.AIResponse;
import com.example.demo.models.AIExplanation;
import com.example.demo.services.dataprovider.ExplanationDataProvider;
import com.example.demo.services.task.AITask;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of MaterialService.
 * Follows Single Responsibility Principle - orchestrates material-related operations.
 * Follows Dependency Inversion Principle - depends on AITask abstraction, not concrete classes.
 * Uses Strategy Pattern via AI Tasks for different operations.
 * Delegates all business logic to AI Tasks.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MaterialServiceImpl implements IMaterialService {

    private final AITask<AIMaterialRequest> recommendationTask;
    private final AITask<AIExplainRequest> explanationTask;
    private final ExplanationDataProvider explanationDataProvider;

    @Override
    public AIResponse recommend(AIMaterialRequest request) {
        log.info("Processing material recommendation request for student: {}, course: {}", 
                request.getStudentId(), request.getCourseId());

        // Delegate to AI Task which handles all data gathering and recommendation generation
        AIResponse response = recommendationTask.execute(request);

        log.info("Material recommendation completed for student: {}, course: {}",
                request.getStudentId(), request.getCourseId());

        return response;
    }

    @Override
    public AIResponse explain(AIExplainRequest request) {
        log.info("Processing material explanation request for student: {}, material: {}",
                request.getStudentId(), request.getMaterialId());

        // Delegate to AI Task which handles all data gathering and explanation generation
        AIResponse response = explanationTask.execute(request);

        log.info("Material explanation completed for student: {}, material: {}",
                request.getStudentId(), request.getMaterialId());

        return response;
    }

    @Override
    public List<AIExplanation> getExplainHistory(Long studentId, Long materialId) {
        log.info("Fetching explanation history for student: {}, material: {}", studentId, materialId);

        // Delegate data fetching to ExplanationDataProvider (SRP compliance)
        List<AIExplanation> explanations = explanationDataProvider.getExplanationHistory(studentId, materialId);

        log.info("Found {} explanations for student: {}, material: {}",
                explanations.size(), studentId, materialId);

        return explanations;
    }
}
