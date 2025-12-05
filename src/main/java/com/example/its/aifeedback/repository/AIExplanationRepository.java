package com.example.its.aifeedback.repository;

import com.example.its.aifeedback.domain.AIExplanation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AIExplanationRepository extends JpaRepository<AIExplanation, Long> {

    List<AIExplanation> findByStudentIdAndMaterialIdOrderByCreatedAtAsc(Long studentId, Long materialId);
}

