package com.example.demo.services.material;

import java.util.List;

import com.example.demo.dto.AIExplainResponseDTO;
import com.example.demo.dto.MaterialContentResponseDTO;
import com.example.demo.dto.request.AIExplainRequest;
import com.example.demo.models.AIExplanation;
import org.springframework.stereotype.Service;

import com.example.demo.dto.material.ChapterDTO;
import com.example.demo.dto.material.MaterialDTO;
import com.example.demo.dto.request.AIMaterialRequest;
import com.example.demo.dto.AIResponse;
import com.example.demo.services.dataprovider.CourseDataProvider;
import com.example.demo.services.dataprovider.ExplanationDataProvider;
import com.example.demo.services.dataprovider.RecommendationDataProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * Implementation of MaterialService.
 * Follows Single Responsibility Principle - orchestrates material-related operations.
 * Uses Strategy Pattern via AI Tasks for different operations.
 * Delegates data fetching to DataProviders (SRP compliance).
 */
@Service
@RequiredArgsConstructor
@Slf4j
// act as a facade for material-related operations
public class MaterialServiceImpl implements IMaterialService {

    private final MaterialRecommendationTask recommendationTask;
    private final MaterialExplanationTask explanationTask;
    
    // DataProviders for SOLID compliance - separate data access concerns
    private final CourseDataProvider courseDataProvider;
    private final ExplanationDataProvider explanationDataProvider;
    private final RecommendationDataProvider recommendationDataProvider;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public AIResponse recommend(AIMaterialRequest request) {
        log.info("Processing material recommendation request for student: {}, course: {}", 
                request.getStudentId(), request.getCourseId());

        // Delegate data fetching to CourseDataProvider (SRP compliance)
        List<ChapterDTO> chapters = courseDataProvider.getMaterialChapters(request.getCourseId());
        log.info("Fetched {} chapters for course: {}", chapters.size(), request.getCourseId());

        List<MaterialDTO> allMaterials = courseDataProvider.getMaterialsForChapters(chapters);
        log.info("Fetched {} total materials from {} chapters", allMaterials.size(), chapters.size());

        // Execute the recommendation task with materials context
        AIResponse response = recommendationTask.execute(request, chapters, allMaterials);

        // Delegate saving to RecommendationDataProvider (SRP compliance)
        recommendationDataProvider.saveRecommendation(request, response);

        return response;
    }

    @Override
    public AIResponse explain(AIExplainRequest request) {
        log.info("Processing material explanation request for student: {}, material: {}",
                request.getStudentId(), request.getMaterialId());

        // Delegate data fetching to ExplanationDataProvider (SRP compliance)
        List<AIExplanation> previousExplanations = explanationDataProvider.getPreviousExplanations(
                request.getStudentId(),
                request.getMaterialId()
        );

        List<String> previousQuestions = previousExplanations.stream()
                .map(AIExplanation::getStudentQuestion)
                .toList();

        List<String> previousAnswers = previousExplanations.stream()
                .map(AIExplanation::getExplanation)
                .toList();

        // Delegate material content fetching to CourseDataProvider (SRP compliance)
        MaterialContentResponseDTO materialContent = courseDataProvider.getMaterialContent(request.getMaterialId());

        // Execute the explanation generation task with previous context and material content
        AIResponse response = explanationTask.execute(request, previousQuestions, previousAnswers, materialContent);

        // Delegate saving to ExplanationDataProvider (SRP compliance)
        if (response != null && response.getResult() != null) {
            explanationDataProvider.saveExplanation(
                    request.getStudentId(),
                    request.getMaterialId(),
                    request.getStudentQuestion(),
                    response.getResult()
            );
        }

        return response;
    }

    @Override
    public List<AIExplainResponseDTO> getExplainHistory(Long studentId, Long materialId) {
        log.info("Fetching explanation history for student: {}, material: {}", studentId, materialId);

        // Delegate data fetching to ExplanationDataProvider (SRP compliance)
        List<AIExplanation> explanations = explanationDataProvider.getExplanationHistory(studentId, materialId);

        List<AIExplainResponseDTO> historyDTOs = explanations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        log.info("Found {} explanations for student: {}, material: {}",
                historyDTOs.size(), studentId, materialId);

        return historyDTOs;
    }

    private AIExplainResponseDTO mapToDTO(AIExplanation explanation) {
        return AIExplainResponseDTO.builder()
                .explanationId(explanation.getId())
                .studentId(explanation.getStudentId())
                .materialId(explanation.getMaterialId())
                .studentQuestion(explanation.getStudentQuestion())
                .explanation(explanation.getExplanation())
                .createdAt(explanation.getCreatedAt() != null ?
                        explanation.getCreatedAt().format(DATE_FORMATTER) : null)
                .build();
    }
}
