package com.example.demo.services.hint;

import com.example.demo.dto.request.AIHintRequest;
import com.example.demo.dto.AIResponse;
import com.example.demo.dto.HintResponseDTO;

import java.util.List;

/**
 * Service interface for hint-related operations.
 * Follows Interface Segregation Principle (ISP) - provides only hint-specific methods.
 */
public interface HintService {

    /**
     * Generate a hint for the given problem/question.
     *
     * @param request the AI hint request containing course, assessment, and question identifiers
     * @return AI response with the hint
     */
    AIResponse hint(AIHintRequest request);

    /**
     * Get all hints for a specific student and question.
     *
     * @param studentId the student identifier
     * @param questionId the question identifier
     * @return list of hints ordered by creation time
     */
    List<HintResponseDTO> getHintHistory(Long studentId, Long questionId);
}
