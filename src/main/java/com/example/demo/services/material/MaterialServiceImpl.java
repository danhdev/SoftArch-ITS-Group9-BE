package com.example.demo.services.material;

import com.example.demo.dto.AIExplainResponseDTO;
import com.example.demo.dto.MaterialContentResponseDTO;
import com.example.demo.dto.request.AIExplainRequest;
import com.example.demo.models.AIExplanation;
import com.example.demo.proxy.MaterialProxyClient;
import com.example.demo.repository.AIExplanationRepository;
import org.springframework.stereotype.Service;

import com.example.demo.dto.request.AIFeedbackRequest;
import com.example.demo.dto.AIResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private final AIExplanationRepository explanationRepository;
    private final MaterialProxyClient materialProxyClient;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public AIResponse recommend(AIFeedbackRequest request) {
        log.info("Processing material recommendation request for student: {}", request.getStudentId());
        return recommendationTask.execute(request);
    }

    @Override
    public AIResponse explain(AIExplainRequest request) {
        log.info("Processing material explanation request for student: {}, material: {}",
                request.getStudentId(), request.getMaterialId());

        // Fetch previous explanations from database before generating new explanation
        List<AIExplanation> previousExplanations = getPreviousExplanations(
                request.getStudentId(),
                request.getMaterialId()
        );

        List<String> previousQuestions = previousExplanations.stream()
                .map(AIExplanation::getStudentQuestion)
                .toList();

        List<String> previousAnswers = previousExplanations.stream()
                .map(AIExplanation::getExplanation)
                .toList();

        // Fetch material content from external API
        MaterialContentResponseDTO materialContent = fetchMaterialContent(request.getMaterialId());

        // Execute the explanation generation task with previous context and material content
        AIResponse response = explanationTask.execute(request, previousQuestions, previousAnswers, materialContent);

        // Save the generated explanation to database
        if (response != null && response.getResult() != null) {
            saveExplanationToDatabase(
                    request.getStudentId(),
                    request.getMaterialId(),
                    request.getStudentQuestion(),
                    response.getResult()
            );
        }

        return response;
    }

    /**
     * Fetch previous explanations from database for the student and material.
     * @param studentId the student identifier
     * @param materialId the material identifier
     * @return list of previous explanations
     */
    private List<AIExplanation> getPreviousExplanations(Long studentId, Long materialId) {
        try {
            if (studentId == null || materialId == null) {
                return new ArrayList<>();
            }
            List<AIExplanation> explanations = explanationRepository
                    .findByStudentIdAndMaterialIdOrderByCreatedAtAsc(studentId, materialId);
            log.info("Fetched {} previous explanations for student: {}, material: {}",
                    explanations.size(), studentId, materialId);
            return explanations;
        } catch (Exception e) {
            log.warn("Failed to fetch previous explanations for student: {}, material: {}. Error: {}",
                    studentId, materialId, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Fetch material content from external API.
     * @param materialId the material identifier
     * @return material content response or null if fetch fails
     */
    private MaterialContentResponseDTO fetchMaterialContent(Long materialId) {
        try {
            var response = materialProxyClient.getMaterialContent(materialId);

            if (response != null && response.getData() != null) {
                MaterialContentResponseDTO content = response.getData();
                log.info("Fetched material content: file={}, pages={}",
                        content.getFileName(), content.getPages());
                return content;
            }

            log.warn("No material content found for material: {}", materialId);
            return null;
        } catch (Exception e) {
            log.warn("Failed to fetch material content for material: {}. Error: {}",
                    materialId, e.getMessage());
            return null;
        }
    }

    /**
     * Save the generated explanation to the database.
     * @param studentId the student identifier
     * @param materialId the material identifier
     * @param studentQuestion the student's question
     * @param explanation the generated explanation
     */
    private void saveExplanationToDatabase(Long studentId, Long materialId,
                                           String studentQuestion, String explanation) {
        try {
            if (studentId == null || materialId == null || explanation == null) {
                log.warn("Cannot save explanation: missing required fields (studentId: {}, materialId: {}, explanation: {})",
                        studentId, materialId, explanation != null ? "present" : "null");
                return;
            }

            AIExplanation explanationEntity = AIExplanation.builder()
                    .studentId(studentId)
                    .materialId(materialId)
                    .studentQuestion(studentQuestion)
                    .explanation(explanation)
                    .build();

            AIExplanation savedExplanation = explanationRepository.save(explanationEntity);
            log.info("Saved explanation to database with ID: {} for student: {}, material: {}",
                    savedExplanation.getId(), studentId, materialId);
        } catch (Exception e) {
            log.error("Failed to save explanation to database for student: {}, material: {}. Error: {}",
                    studentId, materialId, e.getMessage(), e);
        }
    }

    @Override
    public List<AIExplainResponseDTO> getExplainHistory(Long studentId, Long materialId) {
        log.info("Fetching explanation history for student: {}, material: {}", studentId, materialId);

        List<AIExplanation> explanations = explanationRepository
                .findByStudentIdAndMaterialIdOrderByCreatedAtAsc(studentId, materialId);

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
