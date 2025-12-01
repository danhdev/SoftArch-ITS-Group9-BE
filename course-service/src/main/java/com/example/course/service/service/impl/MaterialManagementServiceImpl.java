package com.example.course.service.service.impl;

import com.example.course.service.dto.MaterialDTO;
import com.example.course.service.mapper.MaterialMapper;
import com.example.course.service.model.material.Material;
import com.example.course.service.repository.IMaterialRepository;
import com.example.course.service.service.IMaterialManagementService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class MaterialManagementServiceImpl implements IMaterialManagementService {

    private final IMaterialRepository materialRepository;
    private final MaterialMapper materialMapper;

    @Override
    public void createMaterial(String chapterId, MaterialDTO dto) {
        Material material = materialMapper.toEntity(dto);
        material.setChapterId(chapterId);
        materialRepository.save(material);
    }

    @Override
    public void updateMaterial(String materialId, MaterialDTO dto) {
        Material existing = materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Material not found with id: " + materialId));
        materialMapper.updateEntityFromDTO(existing, dto);

        materialRepository.save(existing);
    }

    @Override
    public void deleteMaterial(String materialId) {
        materialRepository.deleteById(materialId);
    }

    @Override
    public List<MaterialDTO> listMaterials(String chapterId) {
        return materialRepository.findByChapterId(chapterId)
                .stream()
                .map(materialMapper::toDTO)
                .collect(Collectors.toList());
    }

}