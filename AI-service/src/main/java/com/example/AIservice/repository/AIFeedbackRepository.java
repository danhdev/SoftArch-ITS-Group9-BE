package com.example.AIservice.repository;

import com.example.AIservice.domain.AIFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AIFeedbackRepository extends JpaRepository<AIFeedback, Long> {

    // Find all feedback entries by student ID
    List<AIFeedback> findByStudentId(Long studentId);

    // Find the latest feedback entry for a student
    @Query("SELECT f FROM AIFeedback f WHERE f.studentId = :studentId ORDER BY f.id DESC")
    Optional<AIFeedback> findLatestByStudentId(@Param("studentId") Long studentId);
}
