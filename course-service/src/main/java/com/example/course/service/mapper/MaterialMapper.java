package com.example.course.service.mapper;

import com.example.course.service.dto.MaterialDTO;
import com.example.course.service.model.material.InteractiveMaterial;
import com.example.course.service.model.material.Material;
import com.example.course.service.model.material.TextMaterial;
import com.example.course.service.model.material.VideoMaterial;
import org.springframework.stereotype.Component;

@Component
public class MaterialMapper {

    /**
     * Chuyển từ DTO sang Entity (Dùng cho Create)
     */
    public Material toEntity(MaterialDTO dto) {
        if (dto == null) return null;

        Material material;
        String type = dto.getType() != null ? dto.getType().toUpperCase() : "";

        switch (type) {
            case "VIDEO":
                VideoMaterial vid = new VideoMaterial();
                vid.setVideoUrl(dto.getContentOrUrl());
                // Giả định metadata chứa transcript hoặc duration, bạn cần parse nếu logic phức tạp hơn
                vid.setTranscript(dto.getMetadata());
                material = vid;
                break;
            case "TEXT":
                TextMaterial text = new TextMaterial();
                text.setContentBody(dto.getContentOrUrl());
                material = text;
                break;
            case "INTERACTIVE":
                InteractiveMaterial interactive = new InteractiveMaterial();
                interactive.setExerciseData(dto.getContentOrUrl());
                material = interactive;
                break;
            default:
                throw new IllegalArgumentException("Unsupported material type: " + type);
        }

        material.setTitle(dto.getTitle());
        return material;
    }

    /**
     * Chuyển từ Entity sang DTO (Dùng cho Read)
     */
    public MaterialDTO toDTO(Material entity) {
        if (entity == null) return null;

        MaterialDTO dto = new MaterialDTO();
        dto.setTitle(entity.getTitle());

        // Mapping dựa trên class cụ thể
        if (entity instanceof VideoMaterial) {
            VideoMaterial vid = (VideoMaterial) entity;
            dto.setType("VIDEO");
            dto.setContentOrUrl(vid.getVideoUrl());
            dto.setMetadata(vid.getTranscript());
        } else if (entity instanceof TextMaterial) {
            dto.setType("TEXT");
            dto.setContentOrUrl(((TextMaterial) entity).getContentBody());
        } else if (entity instanceof InteractiveMaterial) {
            dto.setType("INTERACTIVE");
            dto.setContentOrUrl(((InteractiveMaterial) entity).getExerciseData());
        } else {
            // Trường hợp fallback nếu có loại material mới mà chưa handle
            dto.setType("UNKNOWN");
        }

        return dto;
    }

    /**
     * Cập nhật data từ DTO vào Entity đã có (Dùng cho Update)
     */
    public void updateEntityFromDTO(Material existingEntity, MaterialDTO dto) {
        if (existingEntity == null || dto == null) return;

        // Cập nhật field chung
        existingEntity.setTitle(dto.getTitle());

        // Cập nhật field riêng
        if (existingEntity instanceof VideoMaterial && "VIDEO".equalsIgnoreCase(dto.getType())) {
            VideoMaterial vid = (VideoMaterial) existingEntity;
            vid.setVideoUrl(dto.getContentOrUrl());
            vid.setTranscript(dto.getMetadata());
        } else if (existingEntity instanceof TextMaterial && "TEXT".equalsIgnoreCase(dto.getType())) {
            ((TextMaterial) existingEntity).setContentBody(dto.getContentOrUrl());
        } else if (existingEntity instanceof InteractiveMaterial && "INTERACTIVE".equalsIgnoreCase(dto.getType())) {
            ((InteractiveMaterial) existingEntity).setExerciseData(dto.getContentOrUrl());
        }
    }
}
