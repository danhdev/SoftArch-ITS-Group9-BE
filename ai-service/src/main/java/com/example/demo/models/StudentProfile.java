package com.example.demo.models;

import java.util.List;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
// import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a student's learning profile.
 * Tracks skill mastery, weakness areas, and recommended learning path.
 */
@Entity
@Table(name = "student_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfile {

    @Id
    @Column(name = "student_id")
    private String studentId;

    @ElementCollection
    @CollectionTable(name = "skill_mastery", joinColumns = @JoinColumn(name = "student_id"))
    @MapKeyColumn(name = "skill")
    @Column(name = "mastery_level")
    private Map<String, Double> skillMasteryMap;

    @ElementCollection
    @CollectionTable(name = "weakness_areas", joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "weakness")
    private List<String> weaknessAreas;

    @ElementCollection
    @CollectionTable(name = "learning_path", joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "topic")
    private List<String> recommendedLearningPath;
}
