package com.example.course.service.service;

import com.example.course.service.dto.CourseDTO;
import java.util.List;

public interface ICourseManagementService  {
    void createCourse(CourseDTO courseData);
    void updateCourse(String courseId, CourseDTO courseData);
    List<CourseDTO> searchCourses(String keyword);
    List<CourseDTO> getCourseDetail(String courseId);
    void deleteCourse(String courseId);
}
