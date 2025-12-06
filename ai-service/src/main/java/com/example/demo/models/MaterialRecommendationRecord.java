package com.example.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity for storing material recommendation records for students.
 */
@Entity
@Table(name = "material_recommendations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialRecommendationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(name = "course_id", nullable = false)
    private String courseId;

    @Column(name = "chapter_id")
    private String chapterId;

    @Column(name = "recommendation_text", columnDefinition = "TEXT")
    private String recommendationText;

    @Column(name = "preferred_difficulty")
    private String preferredDifficulty;

    @Column(name = "preferred_type")
    private String preferredType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
