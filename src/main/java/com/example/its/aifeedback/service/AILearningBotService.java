package com.example.its.aifeedback.service;

import com.example.its.aifeedback.dto.ChatRequestDTO;
import com.example.its.aifeedback.dto.ChatResponseDTO;
import com.example.its.aifeedback.dto.StudentProfileDTO;

/**
 * ================================
 * AI LEARNING BOT SERVICE INTERFACE
 * ================================
 * 
 * Service interface for AI Learning Bot - Personalized Learning (1.3.5).
 * 
 * Chức năng:
 * - Chat trực tiếp với AI Learning Bot
 * - Phân tích hồ sơ học viên
 * - Gợi ý tài liệu phù hợp với năng lực
 * 
 * SOLID Principles Applied:
 * - ISP: Interface riêng cho chức năng chat
 * - SRP: Chỉ xử lý personalized learning
 * - DIP: Controllers depend on abstraction
 */
public interface AILearningBotService {

    /**
     * Chat trực tiếp với AI Learning Bot.
     * AI sẽ đọc tài liệu liên quan và tạo giải thích theo hồ sơ học viên.
     * 
     * @param request Chat request từ học sinh
     * @return AI response với gợi ý tài liệu
     */
    ChatResponseDTO chat(ChatRequestDTO request);

    /**
     * Phân tích và trả về hồ sơ học tập của học sinh.
     * Bao gồm: điểm mạnh, điểm yếu, tỷ lệ đúng, đề xuất độ khó tiếp theo.
     * 
     * @param studentId ID học sinh
     * @return Profile với phân tích chi tiết
     */
    StudentProfileDTO getStudentProfile(Long studentId);

    /**
     * Gợi ý tài liệu học tập phù hợp với năng lực và chủ đề.
     * 
     * @param studentId ID học sinh
     * @param topic     Chủ đề muốn học
     * @return Danh sách tài liệu được gợi ý
     */
    String suggestMaterials(Long studentId, String topic);
}
