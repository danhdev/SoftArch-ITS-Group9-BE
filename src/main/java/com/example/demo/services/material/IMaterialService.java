package com.example.demo.services.material;

import com.example.demo.dto.AIExplainResponseDTO;
import com.example.demo.dto.request.AIExplainRequest;
import com.example.demo.dto.request.AIFeedbackRequest;
import com.example.demo.dto.AIResponse;

import java.util.List;

/**
 * Service interface for material-related operations.
 * Follows Interface Segregation Principle (ISP) - provides only material-specific methods.
 */
public interface IMaterialService {

    /**
     * Recommend learning materials based on the request.
     *
     * @param request the AI request containing student context
     * @return AI response with recommendations
     */
    AIResponse recommend(AIFeedbackRequest request);

    /**
     * Explain a topic or concept.
     *
     * @param request the AI request containing the topic to explain
     * @return AI response with explanation
     */
    AIResponse explain(AIExplainRequest request);

    /**
     * Get all explanations for a specific student and material.
     *
     * @param studentId the student identifier
     * @param materialId the material identifier
     * @return list of explanations ordered by creation time
     */
    List<AIExplainResponseDTO> getExplainHistory(Long studentId, Long materialId);
}
