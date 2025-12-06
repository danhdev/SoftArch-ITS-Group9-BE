package com.example.demo.services.material;

import com.example.demo.dto.AIExplainResponseDTO;
import com.example.demo.dto.request.AIExplainRequest;
import com.example.demo.models.AIExplanation;
import com.example.demo.repository.AIExplanationRepository;
import org.springframework.stereotype.Service;

import com.example.demo.dto.request.AIFeedbackRequest;
import com.example.demo.dto.AIResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;
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

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public AIResponse recommend(AIFeedbackRequest request) {
        log.info("Processing material recommendation request for student: {}", request.getStudentId());
        return recommendationTask.execute(request);
    }

    @Override
    public AIResponse explain(AIExplainRequest request) {
        log.info("Processing material explanation request");
        return explanationTask.execute(request);
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
