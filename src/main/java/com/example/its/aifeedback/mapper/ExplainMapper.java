package com.example.its.aifeedback.mapper;

import com.example.its.aifeedback.domain.AIExplanation;
import com.example.its.aifeedback.domain.ExplainSubmissionContext;
import com.example.its.aifeedback.dto.AIExplainRequestDTO;
import com.example.its.aifeedback.dto.AIExplainResponseDTO;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExplainMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static AIExplanation toEntity(AIExplainRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        return AIExplanation.builder()
                .studentId(requestDTO.getStudentId())
                .materialId(requestDTO.getMaterialId())
                .studentQuestion(requestDTO.getStudentQuestion())
                .build();
    }

    public static AIExplainResponseDTO toDTO(AIExplanation entity) {
        if (entity == null) {
            return null;
        }

        return AIExplainResponseDTO.builder()
                .explainId(entity.getId())
                .studentId(entity.getStudentId())
                .materialId(entity.getMaterialId())
                .explanation(entity.getExplanation())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().format(DATE_FORMATTER) : null)
                .build();
    }

    public static ExplainSubmissionContext toContext(
            AIExplainRequestDTO requestDTO,
            String materialContent,
            String fileName,
            String pages,
            List<String> previousQuestions,
            List<String> previousExplanations) {
        if (requestDTO == null) {
            return null;
        }

        return ExplainSubmissionContext.builder()
                .studentQuestion(requestDTO.getStudentQuestion())
                .materialContent(materialContent)
                .fileName(fileName)
                .pages(pages)
                .previousQuestions(previousQuestions)
                .previousExplanations(previousExplanations)
                .build();
    }

    public static ExplainSubmissionContext toContext(AIExplainRequestDTO requestDTO) {
        return toContext(requestDTO, null, null, null, null, null);
    }
}

