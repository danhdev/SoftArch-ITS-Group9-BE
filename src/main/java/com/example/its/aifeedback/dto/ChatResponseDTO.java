package com.example.its.aifeedback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for AI Learning Bot chat responses.
 * Contains AI's response to student's question.
 * 
 * Personalized Learning (1.3.5):
 * - Hệ thống (AI) tạo giải thích theo hồ sơ học viên
 * - Gợi ý thêm tài liệu phù hợp với năng lực và chủ đề
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponseDTO {

    private Long studentId;

    /**
     * Tin nhắn gốc của học sinh
     */
    private String userMessage;

    /**
     * Phản hồi từ AI
     */
    private String aiResponse;

    /**
     * Tài liệu được gợi ý (nếu có)
     */
    private String suggestedMaterials;

    /**
     * Timestamp của cuộc trò chuyện
     */
    private LocalDateTime timestamp;
}
