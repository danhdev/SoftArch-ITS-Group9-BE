package com.example.its.aifeedback.mapper;

import com.example.its.aifeedback.domain.AIHint;
import com.example.its.aifeedback.domain.HintSubmissionContext;
import com.example.its.aifeedback.dto.HintRequestDTO;
import com.example.its.aifeedback.dto.HintResponseDTO;
import com.example.its.aifeedback.dto.MaterialDTO;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class HintMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static AIHint toEntity(HintRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        return AIHint.builder()
                .studentId(requestDTO.getStudentId())
                .questionId(requestDTO.getQuestionId())
                .subjectId(requestDTO.getSubjectId())
                .subject(requestDTO.getSubject())
                .topic(requestDTO.getTopic())
                .difficulty(requestDTO.getDifficulty())
                .build();
    }

    public static HintResponseDTO toDTO(AIHint entity) {
        if (entity == null) {
            return null;
        }

        return HintResponseDTO.builder()
                .hintId(entity.getId())
                .questionId(entity.getQuestionId())
                .studentId(entity.getStudentId())
                .hint(entity.getHint())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().format(DATE_FORMATTER) : null)
                .build();
    }

    public static HintResponseDTO toDTO(AIHint entity, Integer hintCount) {
        if (entity == null) {
            return null;
        }

        return HintResponseDTO.builder()
                .hintId(entity.getId())
                .questionId(entity.getQuestionId())
                .studentId(entity.getStudentId())
                .hint(entity.getHint())
                .hintCount(hintCount)
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().format(DATE_FORMATTER) : null)
                .build();
    }

    public static HintSubmissionContext toContext(HintRequestDTO requestDTO, List<String> previousHints) {
        return toContext(requestDTO, previousHints, null);
    }

    public static HintSubmissionContext toContext(HintRequestDTO requestDTO, List<String> previousHints, List<MaterialDTO> materials) {
        if (requestDTO == null) {
            return null;
        }

        return HintSubmissionContext.builder()
                .studentId(requestDTO.getStudentId())
                .questionId(requestDTO.getQuestionId())
                .questionText(requestDTO.getQuestionText())
                .correctAnswer(requestDTO.getCorrectAnswer())
                .subject(requestDTO.getSubject())
                .topic(requestDTO.getTopic())
                .difficulty(requestDTO.getDifficulty())
                .previousHints(previousHints)
                .materials(materials)
                .build();
    }
}

