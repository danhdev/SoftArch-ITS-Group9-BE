package com.example.its.aifeedback.service.impl;

import com.example.its.aifeedback.domain.AIFeedback;
import com.example.its.aifeedback.dto.ChatRequestDTO;
import com.example.its.aifeedback.dto.ChatResponseDTO;
import com.example.its.aifeedback.dto.StudentProfileDTO;
import com.example.its.aifeedback.engine.AIEngine;
import com.example.its.aifeedback.repository.AIFeedbackRepository;
import com.example.its.aifeedback.service.AILearningBotService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ================================
 * AI LEARNING BOT SERVICE IMPLEMENTATION
 * ================================
 * 
 * Implementation của AILearningBotService - Personalized Learning (1.3.5).
 * 
 * Chức năng:
 * - Người học trao đổi trực tiếp với AI Learning Bot để được hỗ trợ
 * - Hệ thống (AI) đọc tài liệu liên quan và tạo giải thích theo hồ sơ học viên
 * - Gợi ý thêm tài liệu phù hợp với năng lực và chủ đề của người học
 * 
 * SOLID Principles Applied:
 * - SRP: Chỉ xử lý personalized learning và chat
 * - DIP: Phụ thuộc vào AIEngine interface, không phụ thuộc implementation cụ
 * thể
 * - OCP: Có thể thay AIEngine khác mà không đổi code này
 */
@Service
@Transactional
public class AILearningBotServiceImpl implements AILearningBotService {

    private final AIFeedbackRepository feedbackRepository;
    private final AIEngine aiEngine;

    public AILearningBotServiceImpl(AIFeedbackRepository feedbackRepository, AIEngine aiEngine) {
        this.feedbackRepository = feedbackRepository;
        this.aiEngine = aiEngine;
    }

    /**
     * Chat trực tiếp với AI Learning Bot.
     * 
     * Flow:
     * 1. Lấy lịch sử học tập gần đây của học sinh
     * 2. Gửi message + history cho AI engine
     * 3. AI tạo response cá nhân hóa theo hồ sơ
     * 4. Gợi ý thêm tài liệu nếu phù hợp
     */
    @Override
    public ChatResponseDTO chat(ChatRequestDTO request) {
        Long studentId = request.getStudentId();

        // Lấy 10 feedback gần nhất để AI hiểu context học tập
        List<AIFeedback> recentHistory = feedbackRepository.findTop10ByStudentIdOrderByCreatedAtDesc(studentId);

        // Gọi AI engine để chat
        String aiResponse = aiEngine.chat(
                studentId,
                request.getMessage(),
                request.getLearningMaterialContext(),
                recentHistory);

        // Gợi ý tài liệu nếu có topic
        String suggestedMaterials = null;
        if (request.getCurrentTopic() != null) {
            List<String> weakTopics = getWeakTopics(studentId);
            suggestedMaterials = aiEngine.suggestMaterials(studentId, request.getCurrentTopic(), weakTopics);
        }

        return ChatResponseDTO.builder()
                .studentId(studentId)
                .userMessage(request.getMessage())
                .aiResponse(aiResponse)
                .suggestedMaterials(suggestedMaterials)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Phân tích hồ sơ học tập của học sinh.
     * 
     * Phân tích:
     * - Tổng số bài làm
     * - Tỷ lệ đúng
     * - Điểm mạnh (topics làm tốt)
     * - Điểm yếu (topics cần cải thiện)
     * - Đề xuất độ khó phù hợp
     */
    @Override
    @Transactional(readOnly = true)
    public StudentProfileDTO getStudentProfile(Long studentId) {
        // Đếm tổng số bài làm
        long totalAttempts = feedbackRepository.countByStudentId(studentId);

        // Đếm số câu đúng
        long correctAnswers = feedbackRepository.countByStudentIdAndIsCorrect(studentId, true);

        // Tính accuracy
        double accuracy = totalAttempts > 0 ? (double) correctAnswers / totalAttempts * 100 : 0;

        // Phân tích điểm mạnh
        List<Object[]> strongTopics = feedbackRepository.findStrongTopicsByStudentId(studentId);
        String strengths = extractTopicsString(strongTopics, 3);

        // Phân tích điểm yếu
        List<Object[]> weakTopics = feedbackRepository.findWeakTopicsByStudentId(studentId);
        String weaknesses = extractTopicsString(weakTopics, 3);

        // Đề xuất độ khó dựa trên performance
        String recommendedDifficulty = calculateRecommendedDifficulty(accuracy);

        // Đề xuất topic tiếp theo (ưu tiên điểm yếu)
        String nextTopic = weakTopics.isEmpty() ? "Tiếp tục chủ đề hiện tại"
                : (String) weakTopics.get(0)[0];

        return StudentProfileDTO.builder()
                .studentId(studentId)
                .totalAttempts((int) totalAttempts)
                .correctAnswers((int) correctAnswers)
                .accuracy(Math.round(accuracy * 100.0) / 100.0) // Round to 2 decimal places
                .strengths(strengths.isEmpty() ? "Chưa đủ dữ liệu" : strengths)
                .weaknesses(weaknesses.isEmpty() ? "Chưa đủ dữ liệu" : weaknesses)
                .recommendedDifficulty(recommendedDifficulty)
                .recommendedNextTopic(nextTopic)
                .build();
    }

    /**
     * Gợi ý tài liệu học tập phù hợp.
     */
    @Override
    @Transactional(readOnly = true)
    public String suggestMaterials(Long studentId, String topic) {
        List<String> weakTopics = getWeakTopics(studentId);
        return aiEngine.suggestMaterials(studentId, topic, weakTopics);
    }

    // ========== Private Helper Methods ==========

    /**
     * Lấy danh sách topics yếu của học sinh.
     */
    private List<String> getWeakTopics(Long studentId) {
        List<Object[]> weakTopicsData = feedbackRepository.findWeakTopicsByStudentId(studentId);
        List<String> result = new ArrayList<>();
        for (Object[] row : weakTopicsData) {
            if (row[0] != null) {
                result.add((String) row[0]);
            }
            if (result.size() >= 3)
                break;
        }
        return result;
    }

    /**
     * Chuyển đổi list topics thành string.
     */
    private String extractTopicsString(List<Object[]> topicsData, int limit) {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (Object[] row : topicsData) {
            if (row[0] != null) {
                if (count > 0)
                    sb.append(", ");
                sb.append(row[0]);
                count++;
                if (count >= limit)
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * Tính toán độ khó được đề xuất dựa trên accuracy.
     * 
     * Logic:
     * - accuracy >= 80%: Tăng độ khó (hard)
     * - accuracy >= 60%: Giữ nguyên (medium)
     * - accuracy < 60%: Giảm độ khó (easy)
     */
    private String calculateRecommendedDifficulty(double accuracy) {
        if (accuracy >= 80) {
            return "hard";
        } else if (accuracy >= 60) {
            return "medium";
        } else {
            return "easy";
        }
    }
}
