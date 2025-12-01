package com.example.course.service.service;

import com.example.course.service.dto.MaterialRequest;
import com.example.course.service.dto.MaterialResponse;
import java.util.List;

public interface IMaterialManagement {
    MaterialResponse createMaterial(String chapterId, MaterialRequest data);
    MaterialResponse updateMaterial(String materialId, MaterialRequest data);
    void deleteMaterial(String materialId);
    List<MaterialResponse> listMaterials(String chapterId);
}
