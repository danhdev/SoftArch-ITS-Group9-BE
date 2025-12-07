package com.example.demo.services.dataprovider;

import com.example.demo.dto.HintResponseDTO;
import com.example.demo.models.AIHint;

import java.util.List;

/**
 * Interface for hint data persistence operations.
 * Follows Interface Segregation Principle (ISP) - provides only hint-specific data operations.
 * Follows Dependency Inversion Principle (DIP) - high-level modules depend on this abstraction.
 */
public interface HintDataProvider {

    /**
     * Fetch previous hint texts for a student and question.
     *
     * @param studentId  the student identifier
     * @param questionId the question identifier
     * @return list of previous hint texts
     */
    List<String> getPreviousHintTexts(Long studentId, Long questionId);

    /**
     * Fetch previous hints as entities for a student and question.
     *
     * @param studentId  the student identifier
     * @param questionId the question identifier
     * @return list of hint entities
     */
    List<AIHint> getPreviousHints(Long studentId, Long questionId);

    /**
     * Save a hint to the database.
     *
     * @param studentId  the student identifier
     * @param questionId the question identifier
     * @param hintText   the generated hint text
     * @return the saved hint entity or null if save failed
     */
    AIHint saveHint(Long studentId, Long questionId, String hintText);

    /**
     * Get hint history as DTOs.
     *
     * @param studentId  the student identifier
     * @param questionId the question identifier
     * @return list of hint response DTOs
     */
    List<HintResponseDTO> getHintHistoryDTOs(Long studentId, Long questionId);

    /**
     * Get hint history as entities.
     *
     * @param studentId  the student identifier
     * @param questionId the question identifier
     * @return list of hint entities
     */
    List<AIHint> getHintHistory(Long studentId, Long questionId);
}