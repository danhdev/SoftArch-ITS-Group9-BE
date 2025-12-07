package com.example.demo.services.dataprovider;

import com.example.demo.dto.ChapterDTO;
import com.example.demo.dto.MaterialDTO;
import com.example.demo.dto.MaterialContentResponseDTO;

import java.util.List;

/**
 * Interface for fetching course-related data from external services.
 * Follows Interface Segregation Principle (ISP) - provides only course data retrieval methods.
 * Follows Dependency Inversion Principle (DIP) - high-level modules depend on this abstraction.
 */
public interface CourseDataProvider {

    /**
     * Fetch course name by course ID.
     *
     * @param courseId the course identifier
     * @return course name or default value if not found
     */
    String getCourseName(String courseId);

    /**
     * Fetch all chapters for a course.
     *
     * @param courseId the course identifier
     * @return list of chapters
     */
    List<ChapterDTO> getChapters(String courseId);

    /**
     * Fetch all chapters for a course (material API version).
     *
     * @param courseId the course identifier
     * @return list of chapters from material service
     */
    List<com.example.demo.dto.material.ChapterDTO> getMaterialChapters(String courseId);

    /**
     * Fetch all materials for a course (aggregated from all chapters).
     *
     * @param courseId the course identifier
     * @return list of all materials
     */
    List<MaterialDTO> getCourseMaterials(String courseId);

    /**
     * Fetch all materials for chapters (material API version).
     *
     * @param chapters list of chapters
     * @return list of all materials
     */
    List<com.example.demo.dto.material.MaterialDTO> getMaterialsForChapters(
            List<com.example.demo.dto.material.ChapterDTO> chapters);

    /**
     * Fetch material content by material ID.
     *
     * @param materialId the material identifier
     * @return material content or null if not found
     */
    MaterialContentResponseDTO getMaterialContent(Long materialId);
}
