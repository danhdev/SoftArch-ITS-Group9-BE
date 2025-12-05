package com.example.its.aifeedback.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * JPA Entity representing AI-generated feedback for a student's submission.
 * This is the aggregate root for the feedback bounded context.
 * 
 * Stored in database to maintain history of all feedback provided to students,
 * enabling analytics and tracking of student progress over time.
 * 
 * Personalized Learning (1.3.5):
 * - Lưu trữ lịch sử để phân tích hồ sơ học viên
 * - Hỗ trợ cá nhân hóa feedback dựa trên performance
 */
@Entity
@Table(name = "ai_hint")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIHint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "feedback_text", columnDefinition = "TEXT")
    private String feedbackText;

    @Column(name = "hint", columnDefinition = "TEXT")
    private String hint;

    // ========== Thêm fields cho Personalized Learning ==========

    /**
     * Chủ đề của câu hỏi - dùng để phân tích điểm mạnh/yếu
     */
    @Column(name = "topic")
    private String topic;

    /**
     * Môn học
     */
    @Column(name = "subject")
    private String subject;

    /**
     * Độ khó của câu hỏi
     */
    @Column(name = "difficulty")
    private String difficulty;

    /**
     * Kết quả: true = đúng, false = sai
     */
    @Column(name = "is_correct")
    private Boolean isCorrect;

    /**
     * Thời gian tạo feedback
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Set createdAt before persist
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
