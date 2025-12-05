package com.example.its.aifeedback.repository;

import com.example.its.aifeedback.domain.AIHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AIHintRepository extends JpaRepository<AIHint, Long> {

    List<AIHint> findByStudentIdAndQuestionIdOrderByCreatedAtAsc(Long studentId, Long questionId);

}
