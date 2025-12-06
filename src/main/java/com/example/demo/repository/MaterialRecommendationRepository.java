package com.example.demo.repository;

import com.example.demo.models.MaterialRecommendationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for MaterialRecommendationRecord entities.
 */
@Repository
public interface MaterialRecommendationRepository extends JpaRepository<MaterialRecommendationRecord, Long> {

    /**
     * Find all recommendation records for a specific student.
     *
     * @param studentId the student identifier
     * @return list of recommendation records
     */
    List<MaterialRecommendationRecord> findByStudentIdOrderByCreatedAtDesc(String studentId);

    /**
     * Find all recommendation records for a specific student and course.
     *
     * @param studentId the student identifier
     * @param courseId  the course identifier
     * @return list of recommendation records
     */
    List<MaterialRecommendationRecord> findByStudentIdAndCourseIdOrderByCreatedAtDesc(String studentId, String courseId);

    /**
     * Find the most recent recommendation for a student and course.
     *
     * @param studentId the student identifier
     * @param courseId  the course identifier
     * @return optional recommendation record
     */
    Optional<MaterialRecommendationRecord> findFirstByStudentIdAndCourseIdOrderByCreatedAtDesc(String studentId, String courseId);
}
