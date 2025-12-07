package com.example.demo.services.dataprovider;

import com.example.demo.dto.TestResponseDTO;
import com.example.demo.dto.QuestionDTO;

import java.util.Optional;

/**
 * Interface for fetching test/assessment-related data from external services.
 * Follows Interface Segregation Principle (ISP) - provides only test data retrieval methods.
 * Follows Dependency Inversion Principle (DIP) - high-level modules depend on this abstraction.
 */
public interface TestDataProvider {

    /**
     * Fetch test context by course and assessment.
     *
     * @param courseId     the course identifier
     * @param assessmentId the assessment identifier
     * @return test response or null if not found
     */
    TestResponseDTO getTestContext(String courseId, String assessmentId);

    /**
     * Fetch test context by course, assessment, and student.
     *
     * @param courseId     the course identifier
     * @param assessmentId the assessment identifier
     * @param studentId    the student identifier
     * @return test response or null if not found
     */
    TestResponseDTO getTestContextForStudent(String courseId, String assessmentId, String studentId);

    /**
     * Find a specific question from test context.
     *
     * @param testContext the test response containing questions
     * @param questionId  the question identifier
     * @return the question or empty if not found
     */
    Optional<QuestionDTO> findQuestion(TestResponseDTO testContext, Long questionId);
}
