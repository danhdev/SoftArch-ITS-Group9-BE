package com.example.demo.services.material;

import com.example.demo.dto.request.AIMaterialRequest;
import com.example.demo.dto.request.AIExplainRequest;
import com.example.demo.dto.AIResponse;
import com.example.demo.models.AIExplanation;

import java.util.List;

/**
 * Service interface for material-related operations.
 * Follows Interface Segregation Principle (ISP) - provides only material-specific methods.
 */
public interface IMaterialService {

    /**
     * Recommend learning materials based on the request.
     * Fetches course materials from external API and generates AI recommendations.
     *
     * @param request the AI request containing student context and course ID
     * @return AI response with recommendations
     */
    AIResponse recommend(AIMaterialRequest request);

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
     * @return list of explanation entities ordered by creation time
     */
    List<AIExplanation> getExplainHistory(Long studentId, Long materialId);
}
