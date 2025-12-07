package com.example.demo.repository;

import com.example.demo.models.AIHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AIHintRepository extends JpaRepository<AIHint, Long> {

    List<AIHint> findByStudentIdAndQuestionIdOrderByCreatedAtAsc(Long studentId, Long questionId);

}
