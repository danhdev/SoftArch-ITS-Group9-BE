package com.example.demo.services.dataprovider.impl;

import com.example.demo.dto.HintResponseDTO;
import com.example.demo.models.AIHint;
import com.example.demo.repository.AIHintRepository;
import com.example.demo.services.dataprovider.HintDataProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of HintDataProvider.
 * Follows Single Responsibility Principle - handles only hint data persistence operations.
 * Centralizes all hint-related database operations.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class HintDataProviderImpl implements HintDataProvider {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AIHintRepository hintRepository;

    @Override
    public List<String> getPreviousHintTexts(Long studentId, Long questionId) {
        List<AIHint> hints = getPreviousHints(studentId, questionId);
        return hints.stream()
                .map(AIHint::getHint)
                .toList();
    }

    @Override
    public List<AIHint> getPreviousHints(Long studentId, Long questionId) {
        try {
            if (studentId == null || questionId == null) {
                return new ArrayList<>();
            }
            List<AIHint> hints = hintRepository.findByStudentIdAndQuestionIdOrderByCreatedAtAsc(studentId, questionId);
            log.info("Fetched {} previous hints for student: {}, question: {}", hints.size(), studentId, questionId);
            return hints;
        } catch (Exception e) {
            log.warn("Failed to fetch previous hints for student: {}, question: {}. Error: {}",
                    studentId, questionId, e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public AIHint saveHint(Long studentId, Long questionId, String hintText) {
        try {
            if (studentId == null || questionId == null || hintText == null) {
                log.warn("Cannot save hint: missing required fields (studentId: {}, questionId: {}, hintText: {})",
                        studentId, questionId, hintText != null ? "present" : "null");
                return null;
            }

            AIHint hint = AIHint.builder()
                    .studentId(studentId)
                    .questionId(questionId)
                    .hint(hintText)
                    .build();

            AIHint savedHint = hintRepository.save(hint);
            log.info("Saved hint to database with ID: {} for student: {}, question: {}",
                    savedHint.getId(), studentId, questionId);
            return savedHint;
        } catch (Exception e) {
            log.error("Failed to save hint to database for student: {}, question: {}. Error: {}",
                    studentId, questionId, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<HintResponseDTO> getHintHistoryDTOs(Long studentId, Long questionId) {
        log.info("Fetching hint history DTOs for student: {}, question: {}", studentId, questionId);

        List<AIHint> hints = getPreviousHints(studentId, questionId);
        List<HintResponseDTO> hintDTOs = new ArrayList<>();

        for (int i = 0; i < hints.size(); i++) {
            AIHint hint = hints.get(i);
            HintResponseDTO dto = HintResponseDTO.builder()
                    .hintId(hint.getId())
                    .questionId(hint.getQuestionId())
                    .studentId(hint.getStudentId())
                    .hint(hint.getHint())
                    .hintCount(i + 1)
                    .createdAt(hint.getCreatedAt() != null ? hint.getCreatedAt().format(DATE_FORMATTER) : null)
                    .build();
            hintDTOs.add(dto);
        }

        log.info("Found {} hints for student: {}, question: {}", hintDTOs.size(), studentId, questionId);
        return hintDTOs;
    }

    @Override
    public List<AIHint> getHintHistory(Long studentId, Long questionId) {
        return getPreviousHints(studentId, questionId);
    }
}
