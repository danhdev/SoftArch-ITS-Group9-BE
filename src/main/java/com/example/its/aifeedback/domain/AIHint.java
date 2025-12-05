package com.example.its.aifeedback.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Column(name = "hint", columnDefinition = "TEXT")
    private String hint;

    @Column(name = "subject")
    private String subject;

    @Column(name = "topic")
    private String topic;

    @Column(name = "difficulty")
    private String difficulty;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
