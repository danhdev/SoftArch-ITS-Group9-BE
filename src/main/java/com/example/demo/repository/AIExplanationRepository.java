package com.example.demo.repository;

import com.example.demo.models.AIExplanation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AIExplanationRepository extends JpaRepository<AIExplanation, Long> {

    List<AIExplanation> findByStudentIdAndMaterialIdOrderByCreatedAtAsc(Long studentId, Long materialId);
}

