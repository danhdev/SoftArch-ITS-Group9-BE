package com.example.its.aifeedback.repository;

import com.example.its.aifeedback.domain.AIHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AIHintRepository extends JpaRepository<AIHint, Long> {

    List<AIHint> findByStudentIdAndQuestionId(Long studentId, Long questionId);

    @Query("SELECT COUNT(h) FROM AIHint h WHERE h.studentId = :studentId AND h.questionId = :questionId")
    Integer countHintsByStudentAndQuestion(@Param("studentId") Long studentId, @Param("questionId") Long questionId);

    List<AIHint> findByStudentIdOrderByCreatedAtAsc(Long studentId);

    List<AIHint> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    List<AIHint> findByQuestionIdOrderByCreatedAtAsc(Long questionId);

    List<AIHint> findByQuestionIdOrderByCreatedAtDesc(Long questionId);
}
