package com.example.course.service.repository;

import com.example.course.service.model.Material;
import java.util.List;

public interface IMaterialRepository {
    Material findById(String id);
    List<Material> findByChapter(String chapterId);
    Material save(Material material);
    void delete(String id);
}
