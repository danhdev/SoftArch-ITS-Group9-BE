package com.example.its.aifeedback.service;

import com.example.its.aifeedback.dto.AIFeedbackDTO;

import java.util.List;

/**
 * ================================
 * QUERY SERVICE INTERFACE
 * ================================
 * 
 * Service interface for AI feedback queries (read-only operations).
 * 
 * SOLID Principles Applied:
 * - ISP (Interface Segregation): Separated from command operations
 * This interface handles QUERIES (read-only)
 * See AIFeedbackService for COMMANDS (write/modify state)
 * 
 * - SRP (Single Responsibility): Only handles reading feedback data,
 * not generating new feedback
 * 
 * - DIP (Dependency Inversion): Controllers depend on this abstraction,
 * not the concrete implementation
 * 
 * CQRS Pattern Lite:
 * This separation allows:
 * - Read operations can be optimized separately (caching, read replicas)
 * - Write operations don't block read operations
 * - Clearer API boundaries and easier testing
 */
public interface AIFeedbackQueryService {

    /**
     * Retrieves the complete feedback history for a student.
     * This is a QUERY operation - read-only, no side effects.
     * 
     * @param studentId the student's ID
     * @return list of all feedback DTOs for the student
     */
    List<AIFeedbackDTO> getFeedbackHistory(Long studentId);

    /**
     * Retrieves the most recent feedback for a student.
     * This is a QUERY operation - read-only, no side effects.
     * 
     * @param studentId the student's ID
     * @return the latest feedback DTO
     * @throws FeedbackNotFoundException if no feedback exists for the student
     */
    AIFeedbackDTO getLatestFeedback(Long studentId);
}
