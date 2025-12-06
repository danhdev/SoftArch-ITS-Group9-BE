package com.example.its.aifeedback.service.impl;

import com.example.its.aifeedback.domain.AIFeedback;
import com.example.its.aifeedback.dto.AIFeedbackDTO;
import com.example.its.aifeedback.exception.FeedbackNotFoundException;
import com.example.its.aifeedback.repository.AIFeedbackRepository;
import com.example.its.aifeedback.service.AIFeedbackQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ================================
 * QUERY SERVICE IMPLEMENTATION
 * ================================
 * 
 * Implementation of AIFeedbackQueryService handling read-only operations.
 * 
 * SOLID Principles Applied:
 * - SRP: Only handles reading feedback data
 * - ISP: Implements only the query interface, not commands
 * - DIP: Depends on AIFeedbackRepository interface, not implementation
 * 
 * The @Transactional(readOnly = true) annotation:
 * - Optimizes database connections for read operations
 * - Signals intent that this service doesn't modify data
 * - Can enable read replica routing in advanced setups
 */
@Service
@Transactional(readOnly = true)
public class AIFeedbackQueryServiceImpl implements AIFeedbackQueryService {

    // Single dependency - repository for reading data
    private final AIFeedbackRepository feedbackRepository;

    /**
     * Constructor injection for repository dependency.
     * 
     * @param feedbackRepository repository for reading feedback data
     */
    public AIFeedbackQueryServiceImpl(AIFeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    /**
     * Retrieves all feedback history for a student.
     * 
     * @param studentId the student's ID
     * @return list of all feedback DTOs, may be empty
     */
    @Override
    public List<AIFeedbackDTO> getFeedbackHistory(Long studentId) {
        List<AIFeedback> feedbackList = feedbackRepository.findByStudentId(studentId);

        return feedbackList.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the most recent feedback for a student.
     * 
     * @param studentId the student's ID
     * @return the latest feedback DTO
     * @throws FeedbackNotFoundException if no feedback exists
     */
    @Override
    public AIFeedbackDTO getLatestFeedback(Long studentId) {
        return feedbackRepository.findTopByStudentIdOrderByIdDesc(studentId)
                .map(this::mapToDTO)
                .orElseThrow(() -> new FeedbackNotFoundException(
                        "No feedback found for student with ID: " + studentId));
    }

    // ========== Private Mapping Methods ==========

    /**
     * Maps domain feedback entity to DTO.
     * Note: This mapper is duplicated from AIFeedbackServiceImpl.
     * In a larger project, consider extracting to a shared Mapper class
     * or using MapStruct for automated mapping.
     */
    private AIFeedbackDTO mapToDTO(AIFeedback feedback) {
        return AIFeedbackDTO.builder()
                .id(feedback.getId())
                .studentId(feedback.getStudentId())
                .questionId(feedback.getQuestionId())
                .feedbackText(feedback.getFeedbackText())
                .hint(feedback.getHint())
                .build();
    }
}
