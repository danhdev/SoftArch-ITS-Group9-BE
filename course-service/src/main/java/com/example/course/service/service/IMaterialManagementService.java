package com.example.course.service.service;

import com.example.course.service.dto.MaterialDTO;

import java.util.List;

public interface IMaterialManagementService {
    void createMaterial(String chapterId, MaterialDTO materialData);

    List<MaterialDTO> listMaterials(String chapterId);

    void deleteMaterial(String materialId);

    void updateMaterial(String materialId, MaterialDTO materialData);
}
