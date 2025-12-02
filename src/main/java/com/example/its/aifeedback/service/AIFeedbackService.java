package com.example.its.aifeedback.service;

import com.example.its.aifeedback.dto.AIFeedbackDTO;
import com.example.its.aifeedback.dto.AIFeedbackRequestDTO;
import com.example.its.aifeedback.dto.RecommendationDTO;

import java.util.List;

/**
 * ================================
 * COMMAND SERVICE INTERFACE
 * ================================
 * 
 * Service interface for AI feedback generation (write operations).
 * 
 * SOLID Principles Applied:
 * - ISP (Interface Segregation): Separated from query operations
 * This interface handles COMMANDS (write/modify state)
 * See AIFeedbackQueryService for QUERIES (read-only)
 * 
 * - SRP (Single Responsibility): Only handles feedback generation
 * and recommendations, not reading historical data
 * 
 * - DIP (Dependency Inversion): Controllers depend on this abstraction,
 * not the concrete implementation
 * 
 * CQRS Pattern Lite:
 * This separation follows CQRS (Command Query Responsibility Segregation)
 * principles, allowing:
 * - Independent scaling of read/write operations
 * - Different optimization strategies for each
 * - Clearer code organization and easier testing
 */
public interface AIFeedbackService {

    /**
     * Generates AI feedback for a student's submission.
     * This is a COMMAND operation that:
     * 1. Processes the submission through AI engine
     * 2. Saves the feedback to database
     * 3. Returns the generated feedback
     * 
     * @param request the submission details
     * @return generated feedback DTO
     */
    AIFeedbackDTO generateFeedback(AIFeedbackRequestDTO request);

    /**
     * Gets learning recommendations for a student.
     * While this reads AI-generated data, it's considered a command service
     * responsibility as it triggers AI processing.
     * 
     * @param studentId the student's ID
     * @return list of recommended next steps
     */
    List<RecommendationDTO> getRecommendations(Long studentId);
}
