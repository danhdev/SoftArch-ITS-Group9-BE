package com.example.its.aifeedback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for student learning profile/analytics.
 * Contains summary of student's learning history and performance.
 * 
 * Personalized Learning (1.3.5):
 * - Hệ thống đọc hồ sơ học viên để cá nhân hóa feedback
 * - Gợi ý dựa trên năng lực và chủ đề của người học
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfileDTO {

    private Long studentId;

    /**
     * Tổng số bài đã làm
     */
    private int totalAttempts;

    /**
     * Số câu trả lời đúng
     */
    private int correctAnswers;

    /**
     * Tỷ lệ đúng (%)
     */
    private double accuracy;

    /**
     * Điểm mạnh - topics làm tốt
     */
    private String strengths;

    /**
     * Điểm yếu - topics cần cải thiện
     */
    private String weaknesses;

    /**
     * Độ khó được đề xuất
     */
    private String recommendedDifficulty;

    /**
     * Chủ đề nên học tiếp
     */
    private String recommendedNextTopic;
}
