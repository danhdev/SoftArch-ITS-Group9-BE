package com.example.course.service.mapper;

import com.example.course.service.dto.CourseDTO;
import com.example.course.service.model.course.Course;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {

    /**
     * Convert Entity (Course) → DTO (CourseDTO)
     */
    public CourseDTO toDTO(Course course) {
        if (course == null) {
            return null;
        }
        
        CourseDTO dto = new CourseDTO();
        dto.setName(course.getName());
        dto.setDescription(course.getDescription());
        dto.setCourseStatus(course.getCourseStatus());
        dto.setTeacherId(course.getTeacherId());
        
        return dto;
    }

    /**
     * Convert DTO (CourseDTO) → Entity (Course)
     */
    public Course toEntity(CourseDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Course course = new Course();
        course.setName(dto.getName());
        course.setDescription(dto.getDescription());
        course.setCourseStatus(dto.getCourseStatus());
        course.setTeacherId(dto.getTeacherId());
        
        return course;
    }

    /**
     * Update Entity từ DTO (dùng khi update)
     */
    public void updateEntityFromDTO(Course existing, CourseDTO dto) {
        if (dto == null || existing == null) {
            return;
        }
        // Only update fields that are non-null in the DTO so existing values are preserved
        if (dto.getName() != null) {
            existing.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            existing.setDescription(dto.getDescription());
        }
        if (dto.getCourseStatus() != null) {
            existing.setCourseStatus(dto.getCourseStatus());
        }
        if (dto.getTeacherId() != null) {
            existing.setTeacherId(dto.getTeacherId());
        }
    }
}