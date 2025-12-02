package com.example.AIservice.service.implement;

import com.example.AIservice.domain.AIFeedback;
import com.example.AIservice.dto.AIFeedbackDTO;
import com.example.AIservice.repository.AIFeedbackRepository;
import com.example.AIservice.service.AIFeedbackQueryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AIFeedbackQueryServiceImpl implements AIFeedbackQueryService {

    private final AIFeedbackRepository feedbackRepository;

    public AIFeedbackQueryServiceImpl(AIFeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @Override
    public List<AIFeedbackDTO> getFeedbackHistory(Long studentId) {
        // Retrieve all feedback for the student from repository
        List<AIFeedback> feedbackList = feedbackRepository.findByStudentId(studentId);

        // Convert to DTOs and return
        return feedbackList.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AIFeedbackDTO getLatestFeedback(Long studentId) {
        // Retrieve the latest feedback for the student from repository
        return feedbackRepository.findLatestByStudentId(studentId)
                .map(this::mapToDTO)
                .orElse(null);
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
}

