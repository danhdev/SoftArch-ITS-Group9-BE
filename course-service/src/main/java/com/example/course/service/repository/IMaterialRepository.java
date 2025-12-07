package com.example.course.service.repository;

import com.example.course.service.model.material.Material;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IMaterialRepository extends JpaRepository<Material, String> {
    List<Material> findByChapterId(String chapterId);
}