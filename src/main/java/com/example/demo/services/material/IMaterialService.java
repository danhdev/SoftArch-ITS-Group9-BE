package com.example.demo.services.material;

import com.example.demo.dto.request.AIMaterialRequest;
import com.example.demo.dto.request.AIFeedbackRequest;
import com.example.demo.dto.AIResponse;

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
    AIResponse explain(AIFeedbackRequest request);
}
