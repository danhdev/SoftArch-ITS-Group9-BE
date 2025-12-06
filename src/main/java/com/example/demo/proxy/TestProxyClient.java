package com.example.demo.proxy;

import com.example.demo.dto.TestResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
}
