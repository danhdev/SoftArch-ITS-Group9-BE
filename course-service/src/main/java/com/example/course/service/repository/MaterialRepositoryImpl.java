package com.example.course.service.repository;

import com.example.course.service.model.Material;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MaterialRepositoryImpl implements IMaterialRepository {

    private final List<Material> materials = new ArrayList<>();

    @Override
    public Material findById(String id) {
        return materials.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Material> findByChapter(String chapterId) {
        return materials.stream()
                .filter(m -> m.getChapterId().equals(chapterId))
                .toList();
    }

    @Override
    public Material save(Material material) {
        // nếu chưa có id thì gán id random (cho tiện)
        if (material.getId() == null) {
            material.setId(String.valueOf(materials.size() + 1));
        }

        // nếu tồn tại rồi → cập nhật
        Material existing = findById(material.getId());
        if (existing != null) {
            materials.remove(existing);
        }

        materials.add(material);
        return material;
    }

    @Override
    public void delete(String id) {
        materials.removeIf(m -> m.getId().equals(id));
    }
}
