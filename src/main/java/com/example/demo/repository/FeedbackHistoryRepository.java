package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.FeedbackRecord;

/**
 * Repository interface for FeedbackRecord entity.
 * Follows the Interface Segregation Principle (ISP) by extending JpaRepository
 * and adding only necessary custom query methods.
 */
@Repository
public interface FeedbackHistoryRepository extends JpaRepository<FeedbackRecord, Long> {

    /**
     * Find all feedback records for a specific student.
     *
     * @param studentId the student's unique identifier
     * @return list of feedback records
     */
    List<FeedbackRecord> findByStudentId(String studentId);

    /**
     * Find feedback record by student ID and assessment ID.
     *
     * @param studentId    the student's unique identifier
     * @param assessmentId the assessment's unique identifier
     * @return optional feedback record
     */
    Optional<FeedbackRecord> findByStudentIdAndAssessmentId(String studentId, String assessmentId);

    /**
     * Find all feedback records for a specific course.
     *
     * @param courseId the course's unique identifier
     * @return list of feedback records
     */
    List<FeedbackRecord> findByCourseId(String courseId);
}
