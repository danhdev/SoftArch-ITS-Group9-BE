package com.example.its.aifeedback.service.impl;

import com.example.its.aifeedback.domain.AIFeedback;
import com.example.its.aifeedback.domain.LearningRecommendation;
import com.example.its.aifeedback.domain.SubmissionContext;
import com.example.its.aifeedback.dto.AIFeedbackDTO;
import com.example.its.aifeedback.dto.AIFeedbackRequestDTO;
import com.example.its.aifeedback.dto.RecommendationDTO;
import com.example.its.aifeedback.engine.AIEngine;
import com.example.its.aifeedback.repository.AIFeedbackRepository;
import com.example.its.aifeedback.service.AIFeedbackService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ================================
 * COMMAND SERVICE IMPLEMENTATION
 * ================================
 * 
 * Implementation of AIFeedbackService handling write/command operations.
 * 
 * SOLID Principles Applied:
 * - SRP: Only handles feedback generation and recommendations
 * - OCP: New AI engines can be added without modifying this class
 * - DIP: Depends on AIEngine and AIFeedbackRepository interfaces,
 * not concrete implementations (injected via constructor)
 * 
 * The @Service annotation marks this as a Spring-managed bean.
 * Constructor injection is preferred over field injection for:
 * - Explicit dependencies
 * - Easier testing (can inject mocks)
 * - Immutability (final fields)
 */
@Service
@Transactional
public class AIFeedbackServiceImpl implements AIFeedbackService {

    // Dependencies injected via constructor (DIP - Dependency Inversion Principle)
    private final AIFeedbackRepository feedbackRepository;
    private final AIEngine aiEngine;

    /**
     * Constructor injection for dependencies.
     * Spring automatically injects implementations of these interfaces.
     * 
     * @param feedbackRepository repository for persisting feedback
     * @param aiEngine           AI engine for generating feedback (Strategy
     *                           Pattern)
     */
    public AIFeedbackServiceImpl(AIFeedbackRepository feedbackRepository, AIEngine aiEngine) {
        this.feedbackRepository = feedbackRepository;
        this.aiEngine = aiEngine;
    }

    /**
     * Generates feedback for a student's submission.
     * 
     * Flow:
     * 1. Map DTO → Domain model (SubmissionContext)
     * 2. Call AI engine to generate feedback
     * 3. Bổ sung thông tin cho Personalized Learning
     * 4. Persist feedback to database
     * 5. Map Domain → DTO for response
     * 
     * @param request the submission request DTO
     * @return generated feedback DTO
     */
    @Override
    public AIFeedbackDTO generateFeedback(AIFeedbackRequestDTO request) {
        // Step 1: Map request DTO to domain context
        SubmissionContext context = mapToContext(request);

        // Step 2: Generate feedback using AI engine (Strategy Pattern in action)
        AIFeedback feedback = aiEngine.generateFeedback(context);

        // Step 3: Bổ sung thông tin cho Personalized Learning (1.3.5)
        // Lưu thêm topic, subject, difficulty để phân tích hồ sơ học viên
        feedback.setTopic(request.getTopic());
        feedback.setSubject(request.getSubject());
        feedback.setDifficulty(request.getDifficulty());

        // Xác định đúng/sai để tính accuracy
        boolean isCorrect = isAnswerCorrect(request.getStudentAnswer(), request.getCorrectAnswer());
        feedback.setIsCorrect(isCorrect);

        // Step 4: Persist the feedback
        AIFeedback savedFeedback = feedbackRepository.save(feedback);

        // Step 5: Map to DTO and return
        return mapToDTO(savedFeedback);
    }

    /**
     * Kiểm tra câu trả lời đúng hay sai.
     * So sánh không phân biệt hoa thường, bỏ khoảng trắng thừa.
     */
    private boolean isAnswerCorrect(String studentAnswer, String correctAnswer) {
        if (studentAnswer == null || correctAnswer == null) {
            return false;
        }
        String normalized1 = studentAnswer.trim().toLowerCase();
        String normalized2 = correctAnswer.trim().toLowerCase();
        return normalized1.equals(normalized2);
    }

    /**
     * Gets learning recommendations for a student.
     * 
     * TIÊU CHÍ ĐỀ XUẤT (Personalized Learning 1.3.5):
     * 1. Phân tích tỷ lệ đúng/sai tổng thể → xác định năng lực
     * 2. Phân tích topic làm sai nhiều → điểm yếu cần cải thiện
     * 3. Phân tích topic làm đúng nhiều → điểm mạnh có thể nâng cao
     * 4. Xem xét lịch sử gần đây → xu hướng tiến bộ
     * 5. Dựa vào các yếu tố trên → đề xuất chủ đề tiếp theo phù hợp
     * 
     * @param studentId the student's ID
     * @return list of recommendation DTOs
     */
    @Override
    public List<RecommendationDTO> getRecommendations(Long studentId) {
        // Step 1: Thu thập dữ liệu phân tích từ database
        long totalAttempts = feedbackRepository.countByStudentId(studentId);
        long correctCount = feedbackRepository.countByStudentIdAndIsCorrect(studentId, true);
        
        // Step 2: Lấy danh sách topic mạnh/yếu
        List<Object[]> strongTopics = feedbackRepository.findStrongTopicsByStudentId(studentId);
        List<Object[]> weakTopics = feedbackRepository.findWeakTopicsByStudentId(studentId);
        
        // Step 3: Lấy lịch sử gần đây để xem xu hướng
        List<AIFeedback> recentHistory = feedbackRepository.findTop10ByStudentIdOrderByCreatedAtDesc(studentId);
        
        // Step 4: Gọi AI Engine với đầy đủ dữ liệu phân tích
        List<LearningRecommendation> recommendations = aiEngine.suggestNextSteps(
                studentId,
                totalAttempts,
                correctCount,
                strongTopics,
                weakTopics,
                recentHistory
        );

        // Step 5: Map to DTOs
        return recommendations.stream()
                .map(this::mapToRecommendationDTO)
                .collect(Collectors.toList());
    }

    // ========== Private Mapping Methods ==========

    /**
     * Maps request DTO to domain submission context.
     * Encapsulates mapping logic in one place (SRP).
     */
    private SubmissionContext mapToContext(AIFeedbackRequestDTO request) {
        return SubmissionContext.builder()
                .studentId(request.getStudentId())
                .questionId(request.getQuestionId())
                .questionText(request.getQuestionText())
                .studentAnswer(request.getStudentAnswer())
                .correctAnswer(request.getCorrectAnswer())
                .topic(request.getTopic())
                .difficulty(request.getDifficulty())
                .subject(request.getSubject())
                .build();
    }

    /**
     * Maps domain feedback to DTO.
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
     * Maps domain recommendation to DTO.
     */
    private RecommendationDTO mapToRecommendationDTO(LearningRecommendation recommendation) {
        return RecommendationDTO.builder()
                .nextTopic(recommendation.getNextTopic())
                .explanation(recommendation.getExplanation())
                .build();
    }
}
