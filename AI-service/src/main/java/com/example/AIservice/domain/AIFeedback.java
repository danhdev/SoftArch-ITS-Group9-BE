package com.example.AIservice.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;

@Entity
@Table(name = "ai_feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AIFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "student_id")
    Long studentId;

    @Column(name = "question_id")
    Long questionId;

    @Column(name = "feedback_text", columnDefinition = "TEXT")
    String feedbackText;

    @Column(name = "hint", columnDefinition = "TEXT")
    String hint;
}
