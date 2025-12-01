package com.example.course.service.service.impl;

import com.example.course.service.dto.MaterialRequest;
import com.example.course.service.dto.MaterialResponse;
import com.example.course.service.mapper.MaterialMapper;
import com.example.course.service.model.Material;
import com.example.course.service.repository.IMaterialRepository;
import com.example.course.service.service.IMaterialManagement;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaterialManagementImpl implements IMaterialManagement {

    private final IMaterialRepository repo;

    public MaterialManagementImpl(IMaterialRepository repo) {
        this.repo = repo;
    }

    @Override
    public MaterialResponse createMaterial(String chapterId, MaterialRequest data) {
        Material m = MaterialMapper.toEntity(chapterId, data);
        return MaterialMapper.toResponse(repo.save(m));
    }

    @Override
    public MaterialResponse updateMaterial(String materialId, MaterialRequest data) {
        Material m = repo.findById(materialId);
        if (m == null) return null;

        m.setTitle(data.getTitle());
        m.setType(data.getType());
        m.setUrl(data.getUrl());

        return MaterialMapper.toResponse(repo.save(m));
    }

    @Override
    public void deleteMaterial(String materialId) {
        repo.delete(materialId);
    }

    @Override
    public List<MaterialResponse> listMaterials(String chapterId) {
        return repo.findByChapter(chapterId)
                .stream()
                .map(MaterialMapper::toResponse)
                .collect(Collectors.toList());
    }
}
