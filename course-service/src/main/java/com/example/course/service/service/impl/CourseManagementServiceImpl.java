package com.example.course.service.service.impl;

import com.example.course.service.dto.CourseDTO;
import com.example.course.service.mapper.CourseMapper;
import com.example.course.service.model.course.Course;
import com.example.course.service.repository.ICourseRepository;
import com.example.course.service.service.ICourseManagementService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CourseManagementServiceImpl implements ICourseManagementService {

    private final ICourseRepository courseRepository;
    private final CourseMapper courseMapper;

    @Override
    public void createCourse(CourseDTO courseData) {
        Course course = courseMapper.toEntity(courseData);
        courseRepository.save(course);
    }

    @Override
    public void updateCourse(String courseId, CourseDTO courseData) {
        Course existing = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
        courseMapper.updateEntityFromDTO(existing, courseData);
        courseRepository.save(existing);
    }

    @Override
    public void deleteCourse(String courseId) {
        courseRepository.deleteById(courseId);
    }

    @Override
    public List<CourseDTO> getCourseDetail(String courseId) {
        return courseRepository.findById(courseId)
                .map(course -> List.of(courseMapper.toDTO(course)))
                .orElse(List.of());
    }

    public List<CourseDTO> searchCourses(String keyword) {
        return courseRepository.searchByName(keyword).stream()
                .map(courseMapper::toDTO)
                .toList();
    }
}