package com.example.AIservice.service.implement;

import com.example.AIservice.domain.*;
import com.example.AIservice.dto.AIFeedbackDTO;
import com.example.AIservice.dto.AIFeedbackRequestDTO;
import com.example.AIservice.dto.recommendationDTO;
import com.example.AIservice.repository.AIFeedbackRepository;
import com.example.AIservice.service.AIFeedbackService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AIFeedbackServiceImpl implements AIFeedbackService {

    private final AIFeedbackRepository feedbackRepository;
    private final AIEngine aiEngine;

    public AIFeedbackServiceImpl(AIFeedbackRepository feedbackRepository, AIEngine aiEngine) {
        this.feedbackRepository = feedbackRepository;
        this.aiEngine = aiEngine;
    }

    @Override
    public AIFeedbackDTO generateFeedback(AIFeedbackRequestDTO req) {
        // Convert request DTO to SubmissionContext
        SubmissionContext context = mapToContext(req);

        // Generate feedback using AI engine
        AIFeedback feedback = aiEngine.generateFeedback(context);

        // Save feedback to repository
        AIFeedback savedFeedback = feedbackRepository.save(feedback);

        // Convert to DTO and return
        return mapToDTO(savedFeedback);
    }

    @Override
    public List<recommendationDTO> getRecommendations(Long studentId) {
        // Get recommendations from AI engine
        List<LearningRecommendation> recommendations = aiEngine.suggestNextSteps(studentId);

        // Convert to DTOs and return
        return recommendations.stream()
                .map(this::mapToRecommendationDTO)
                .collect(Collectors.toList());
    }

    /**
     * Maps AIFeedbackRequestDTO to SubmissionContext
     */
    private SubmissionContext mapToContext(AIFeedbackRequestDTO req) {
        return SubmissionContext.builder()
                .studentId(req.getStudentId())
                .questionId(req.getQuestionId())
                .questionText(req.getQuestionText())
                .studentAnswer(req.getStudentAnswer())
                .correctAnswer(req.getCorrectAnswer())
                .topic(req.getTopic())
                .difficulty(req.getDifficulty())
                .subject(req.getSubject())
                .build();
    }

    /**
     * Maps AIFeedback domain entity to AIFeedbackDTO
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

    /**
     * Maps LearningRecommendation domain entity to recommendationDTO
     */
    private recommendationDTO mapToRecommendationDTO(LearningRecommendation rec) {
        return recommendationDTO.builder()
                .nextTopic(rec.getNextTopic())
                .recommendation(rec.getExplanation())
                .build();
    }
}
