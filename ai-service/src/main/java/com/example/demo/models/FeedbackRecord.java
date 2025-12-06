package com.example.demo.models;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a feedback record for a student's assessment.
 */
@Entity
@Table(name = "feedback_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(name = "course_id")
    private String courseId;

    @Column(name = "assessment_id")
    private String assessmentId;

    @Column(name = "feedback_text", columnDefinition = "TEXT")
    private String feedbackText;

//    @ElementCollection
//    @CollectionTable(name = "feedback_strengths", joinColumns = @JoinColumn(name = "feedback_id"))
//    @Column(name = "strength")
//    private List<String> strengths;
//
//    @ElementCollection
//    @CollectionTable(name = "feedback_weaknesses", joinColumns = @JoinColumn(name = "feedback_id"))
//    @Column(name = "weakness")
//    private List<String> weaknesses;
//
//    @ElementCollection
//    @CollectionTable(name = "feedback_recommended_topics", joinColumns = @JoinColumn(name = "feedback_id"))
//    @Column(name = "topic")
//    private List<String> recommendedNextTopics;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
