package com.example.demo.proxy;

import com.example.demo.dto.material.ChapterDTO;
import com.example.demo.dto.material.MaterialApiResponse;
import com.example.demo.dto.material.MaterialDTO;
import com.example.demo.dto.ChapterContentResponseDTO;
import com.example.demo.dto.MaterialContentResponseDTO;
import com.example.demo.dto.ResponseObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * OpenFeign client for fetching course materials from external service.
 * This proxy abstracts the external API call, following the Dependency Inversion Principle.
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
     * Fetch list of chapters for a specific course.
     *
     * @param courseId the course identifier
     * @return API response containing list of chapters
     */
    @GetMapping("/course/{courseId}/chapters")
    MaterialApiResponse<List<ChapterDTO>> getChaptersByCourse(
            @PathVariable("courseId") String courseId
    );

    /**
     * Fetch list of materials for a specific chapter.
     *
     * @param chapterId the chapter identifier
     * @return API response containing list of materials
     */
    @GetMapping("/chapters/{chapterId}/materials")
    MaterialApiResponse<List<MaterialDTO>> getMaterialsByChapter(
            @PathVariable("chapterId") String chapterId
    );

     /* Fetch content/materials for a specific chapter.
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

