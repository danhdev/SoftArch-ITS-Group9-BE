package com.example.course.service.service.impl;

import com.example.course.service.dto.MaterialDTO;
import com.example.course.service.exception.ResourceNotFoundException;
import com.example.course.service.mapper.MaterialMapper;
import com.example.course.service.model.chapter.Chapter;
import com.example.course.service.model.material.Material;
import com.example.course.service.repository.IChapterRepository;
import com.example.course.service.repository.IMaterialRepository;
import com.example.course.service.service.IMaterialManagementService;
import com.example.course.service.util.content.ContentReader;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class MaterialManagementServiceImpl implements IMaterialManagementService {

    private final IMaterialRepository materialRepository;
    private final MaterialMapper materialMapper;
    private final ContentReader contentReader;
    private final MinioClient minioClient;
    private final ObjectMapper objectMapper;
    private final IChapterRepository chapterRepository;

    @Override
    public void createMaterial(String chapterId, MaterialDTO dto) {
        Material material = materialMapper.toEntity(dto);
        // material.setChapterId(chapterId);
        Chapter chap = chapterRepository.findById(chapterId).orElse(null);
        material.setChapter(chap);
        materialRepository.save(material);
    }

    @Override
    public void updateMaterial(String materialId, MaterialDTO dto) {
        Material existing = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found with id: " + materialId));
        materialMapper.updateEntityFromDTO(existing, dto);

        materialRepository.save(existing);
    }

    @Override
    public void deleteMaterial(String materialId) {
        if (!materialRepository.existsById(materialId)) {
            throw new ResourceNotFoundException("Cannot delete. Material not found with id: " + materialId);
        }
        materialRepository.deleteById(materialId);
    }

    @Override
    public List<MaterialDTO> listMaterials(String chapterId) {
        return materialRepository.findByChapterId(chapterId)
                .stream()
                .map(materialMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getContent(String id) {
        try (InputStream is = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket("materials")
                        .object(id)
                        .build())) {

            JSONObject contentPDF = contentReader.readContent(is, id);
            return objectMapper.readValue(
                    contentPDF.toString(),
                    new TypeReference<>() {}
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to read content for id: " + id, e);
        }

    }
}