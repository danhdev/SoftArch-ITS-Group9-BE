package com.example.its.aifeedback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for AI Learning Bot chat requests.
 * Used when student wants to interact directly with AI tutor.
 * 
 * Personalized Learning (1.3.5):
 * - Người học trao đổi trực tiếp với AI Learning Bot
 * - AI đọc tài liệu liên quan và tạo giải thích theo hồ sơ học viên
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRequestDTO {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotBlank(message = "Message is required")
    private String message;

    /**
     * Optional: Context về học liệu đang học
     * AI sẽ đọc và giải thích theo context này
     */
    private String learningMaterialContext;

    /**
     * Optional: Chủ đề hiện tại
     */
    private String currentTopic;

    /**
     * Optional: Môn học
     */
    private String subject;
}
