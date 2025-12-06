package com.example.demo.proxy;

import com.example.demo.dto.ChapterContentResponseDTO;
import com.example.demo.dto.MaterialContentResponseDTO;
import com.example.demo.dto.ResponseObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * OpenFeign client for fetching material-related data from external service.
 * This proxy abstracts the external API calls for materials and chapters.
 */
@FeignClient(
        name = "material-service",
        url = "${proxy.test-service.url}"
)
public interface MaterialProxyClient {

    /**
     * Fetch content/materials for a specific chapter.
     *
     * @param courseId  the course identifier
     * @param chapterId the chapter identifier
     * @return ResponseObject containing ChapterContentResponseDTO with list of materials
     */
    @GetMapping("/course/{courseId}/chapters/{chapterId}/materials")
    ResponseObject<ChapterContentResponseDTO> getChapterContent(
            @PathVariable("courseId") String courseId,
            @PathVariable("chapterId") String chapterId
    );

    /**
     * Fetch material content by material ID.
     *
     * @param materialId the material identifier
     * @return ResponseObject containing MaterialContentResponseDTO
     */
    @GetMapping("/materials/{materialId}/content")
    ResponseObject<MaterialContentResponseDTO> getMaterialContent(
            @PathVariable("materialId") Long materialId
    );
}

