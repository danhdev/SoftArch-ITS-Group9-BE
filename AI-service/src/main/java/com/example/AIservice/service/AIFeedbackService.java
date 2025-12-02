package com.example.AIservice.service;

import com.example.AIservice.dto.AIFeedbackDTO;
import com.example.AIservice.dto.AIFeedbackRequestDTO;
import com.example.AIservice.dto.recommendationDTO;

import java.util.List;

/**
 * Service interface for AI Feedback operations
 */
public interface AIFeedbackService {

    /**
     * Generates AI feedback based on student's submission
     * @param req The feedback request containing student answer and question details
     * @return AIFeedbackDTO with generated feedback and hints
     */
    AIFeedbackDTO generateFeedback(AIFeedbackRequestDTO req);

    /**
     * Gets personalized learning recommendations for a student
     * @param studentId The ID of the student
     * @return List of recommendations for the student
     */
    List<recommendationDTO> getRecommendations(Long studentId);
}
