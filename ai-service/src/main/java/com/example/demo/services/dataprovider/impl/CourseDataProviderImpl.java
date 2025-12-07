package com.example.demo.services.dataprovider.impl;

import com.example.demo.dto.*;
import com.example.demo.dto.material.MaterialApiResponse;
import com.example.demo.proxy.MaterialProxyClient;
import com.example.demo.proxy.TestProxyClient;
import com.example.demo.services.dataprovider.CourseDataProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of CourseDataProvider.
 * Follows Single Responsibility Principle - handles only course data fetching from external services.
 * Centralizes all external API calls related to course data.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CourseDataProviderImpl implements CourseDataProvider {

    private static final String DEFAULT_COURSE_NAME = "Not specified";

    private final TestProxyClient testProxyClient;
    private final MaterialProxyClient materialProxyClient;

    @Override
    public String getCourseName(String courseId) {
        try {
            ResponseObject<List<CourseDTO>> response = testProxyClient.getCourse(courseId);
            if (response != null && response.getData() != null && !response.getData().isEmpty()) {
                String courseName = response.getData().get(0).getName();
                log.info("Fetched course name: {} for courseId: {}", courseName, courseId);
                return courseName;
            }
        } catch (Exception e) {
            log.warn("Failed to fetch course name for course: {}. Error: {}", courseId, e.getMessage());
        }
        return DEFAULT_COURSE_NAME;
    }

    @Override
    public List<ChapterDTO> getChapters(String courseId) {
        try {
            ResponseObject<List<ChapterDTO>> response = testProxyClient.getCourseChapters(courseId);
            if (response != null && response.getData() != null) {
                log.info("Fetched {} chapters for course: {}", response.getData().size(), courseId);
                return response.getData();
            }
        } catch (Exception e) {
            log.warn("Failed to fetch chapters for course: {}. Error: {}", courseId, e.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    public List<com.example.demo.dto.material.ChapterDTO> getMaterialChapters(String courseId) {
        try {
            MaterialApiResponse<List<com.example.demo.dto.material.ChapterDTO>> response = 
                    materialProxyClient.getChaptersByCourse(courseId);
            if (response != null && response.getData() != null) {
                log.info("Fetched {} material chapters for course: {}", response.getData().size(), courseId);
                return response.getData();
            }
        } catch (Exception e) {
            log.warn("Failed to fetch material chapters for course: {}. Error: {}", courseId, e.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    public List<MaterialDTO> getCourseMaterials(String courseId) {
        List<MaterialDTO> allMaterials = new ArrayList<>();
        try {
            // Fetch all chapters for the course
            List<ChapterDTO> chapters = getChapters(courseId);

            // Fetch content for each chapter
            for (ChapterDTO chapter : chapters) {
                try {
                    String chapterId = String.valueOf(chapter.getOrderIndex());
                    ResponseObject<ChapterContentResponseDTO> contentResponse =
                            materialProxyClient.getChapterContent(courseId, chapterId);

                    if (contentResponse != null && contentResponse.getData() != null
                            && contentResponse.getData().getData() != null) {
                        List<MaterialDTO> chapterMaterials = contentResponse.getData().getData();
                        allMaterials.addAll(chapterMaterials);
                        log.debug("Fetched {} materials from chapter: {}", chapterMaterials.size(), chapter.getTitle());
                    }
                } catch (Exception e) {
                    log.warn("Failed to fetch content for chapter: {}. Error: {}", chapter.getTitle(), e.getMessage());
                }
            }
            log.info("Total materials fetched: {} for course: {}", allMaterials.size(), courseId);
        } catch (Exception e) {
            log.warn("Failed to fetch course materials for course: {}. Error: {}", courseId, e.getMessage());
        }
        return allMaterials;
    }

    @Override
    public List<com.example.demo.dto.material.MaterialDTO> getMaterialsForChapters(
            List<com.example.demo.dto.material.ChapterDTO> chapters) {
        List<com.example.demo.dto.material.MaterialDTO> allMaterials = new ArrayList<>();

        for (com.example.demo.dto.material.ChapterDTO chapter : chapters) {
            try {
                String chapterId = chapter.getChapterId();
                if (chapterId == null || chapterId.isEmpty()) {
                    log.warn("Chapter has no ID, skipping: {}", chapter.getTitle());
                    continue;
                }

                MaterialApiResponse<List<com.example.demo.dto.material.MaterialDTO>> response = 
                        materialProxyClient.getMaterialsByChapter(chapterId);
                if (response != null && response.getData() != null) {
                    allMaterials.addAll(response.getData());
                    log.debug("Fetched {} materials for chapter: {}", response.getData().size(), chapter.getTitle());
                }
            } catch (Exception e) {
                log.warn("Error fetching materials for chapter: {}. Error: {}", chapter.getTitle(), e.getMessage());
            }
        }

        log.info("Total materials fetched from {} chapters: {}", chapters.size(), allMaterials.size());
        return allMaterials;
    }

    @Override
    public MaterialContentResponseDTO getMaterialContent(Long materialId) {
        try {
            var response = materialProxyClient.getMaterialContent(materialId);

            if (response != null && response.getData() != null) {
                MaterialContentResponseDTO content = response.getData();
                log.info("Fetched material content: file={}, pages={}", content.getFileName(), content.getPages());
                return content;
            }

            log.warn("No material content found for material: {}", materialId);
        } catch (Exception e) {
            log.warn("Failed to fetch material content for material: {}. Error: {}", materialId, e.getMessage());
        }
        return null;
    }
}
