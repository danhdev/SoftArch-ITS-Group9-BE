package com.example.demo.proxy;

import com.example.demo.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * OpenFeign client for fetching test/assessment data from external service.
 * This proxy abstracts the external API call, following the Dependency Inversion Principle.
 */
@FeignClient(
        name = "test-service",
        url = "${proxy.test-service.url}"
)
public interface TestProxyClient {

    /**
     * Fetch test details including questions for a specific course, test, and student.
     *
     * @param courseId  the course identifier
     * @param testId    the test/assessment identifier
     * @param studentId the student identifier
     * @return TestResponseDTO containing test details and questions with student answers
     */
    @GetMapping("/course/{courseId}/test/{testId}/{studentId}")
    TestResponseDTO getTestByStudentAndCourse(
            @PathVariable("courseId") String courseId,
            @PathVariable("testId") String testId,
            @PathVariable("studentId") String studentId
    );

    /**
     * Fetch test details including questions for a specific course and assessment.
     * Used for hint generation where student context is not needed.
     *
     * @param courseId     the course identifier
     * @param assessmentId the test/assessment identifier
     * @return TestResponseDTO containing test details and questions
     */
    @GetMapping("/course/{courseId}/test/{assessmentId}")
    TestResponseDTO getTestByCourseAndAssessment(
            @PathVariable("courseId") String courseId,
            @PathVariable("assessmentId") String assessmentId
    );

    /**
     * Fetch course details by course ID.
     *
     * @param courseId the course identifier
     * @return ResponseObject containing list of CourseDTO
     */
    @GetMapping("/courses/{courseId}")
    ResponseObject<List<CourseDTO>> getCourse(@PathVariable("courseId") String courseId);

    /**
     * Fetch chapters for a specific course.
     *
     * @param courseId the course identifier
     * @return ResponseObject containing list of ChapterDTO
     */
    @GetMapping("/course/{courseId}/chapter")
    ResponseObject<List<ChapterDTO>> getCourseChapters(@PathVariable("courseId") String courseId);

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
