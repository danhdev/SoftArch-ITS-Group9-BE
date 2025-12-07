package com.example.demo.services.dataprovider.impl;

import com.example.demo.dto.AIExplainResponseDTO;
import com.example.demo.models.AIExplanation;
import com.example.demo.repository.AIExplanationRepository;
import com.example.demo.services.dataprovider.ExplanationDataProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ExplanationDataProvider.
 * Follows Single Responsibility Principle - handles only explanation data persistence operations.
 * Centralizes all explanation-related database operations.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ExplanationDataProviderImpl implements ExplanationDataProvider {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AIExplanationRepository explanationRepository;

    @Override
    public List<AIExplanation> getPreviousExplanations(Long studentId, Long materialId) {
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

    @Override
    public List<String> extractQuestions(List<AIExplanation> explanations) {
        return explanations.stream()
                .map(AIExplanation::getStudentQuestion)
                .toList();
    }

    @Override
    public List<String> extractAnswers(List<AIExplanation> explanations) {
        return explanations.stream()
                .map(AIExplanation::getExplanation)
                .toList();
    }

    @Override
    public AIExplanation saveExplanation(Long studentId, Long materialId, String studentQuestion, String explanation) {
        try {
            if (studentId == null || materialId == null || explanation == null) {
                log.warn("Cannot save explanation: missing required fields (studentId: {}, materialId: {}, explanation: {})",
                        studentId, materialId, explanation != null ? "present" : "null");
                return null;
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
            return savedExplanation;
        } catch (Exception e) {
            log.error("Failed to save explanation to database for student: {}, material: {}. Error: {}",
                    studentId, materialId, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<AIExplanation> getExplanationHistory(Long studentId, Long materialId) {
        return getPreviousExplanations(studentId, materialId);
    }

    @Override
    public List<AIExplainResponseDTO> getExplanationHistoryDTOs(Long studentId, Long materialId) {
        log.info("Fetching explanation history DTOs for student: {}, material: {}", studentId, materialId);

        List<AIExplanation> explanations = getPreviousExplanations(studentId, materialId);

        List<AIExplainResponseDTO> historyDTOs = explanations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        log.info("Found {} explanations for student: {}, material: {}", historyDTOs.size(), studentId, materialId);
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
