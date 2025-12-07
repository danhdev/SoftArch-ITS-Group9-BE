package com.example.demo.services.dataprovider.impl;

import com.example.demo.models.AIHint;
import com.example.demo.repository.AIHintRepository;
import com.example.demo.services.dataprovider.HintDataProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
    public List<AIHint> getHintHistory(Long studentId, Long questionId) {
        return getPreviousHints(studentId, questionId);
    }
}
