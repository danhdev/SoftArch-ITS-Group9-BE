package com.example.demo.services.dataprovider.impl;

import com.example.demo.dto.QuestionDTO;
import com.example.demo.dto.TestResponseDTO;
import com.example.demo.proxy.TestProxyClient;
import com.example.demo.services.dataprovider.TestDataProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementation of TestDataProvider.
 * Follows Single Responsibility Principle - handles only test data fetching from external services.
 * Centralizes all external API calls related to test/assessment data.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TestDataProviderImpl implements TestDataProvider {

    private final TestProxyClient testProxyClient;

    @Override
    public TestResponseDTO getTestContext(String courseId, String assessmentId) {
        try {
            TestResponseDTO testContext = testProxyClient.getTestByCourseAndAssessment(courseId, assessmentId);
            log.info("Fetched test context for course: {}, assessment: {}", courseId, assessmentId);
            return testContext;
        } catch (Exception e) {
            log.warn("Failed to fetch test context for course: {}, assessment: {}. Error: {}",
                    courseId, assessmentId, e.getMessage());
            return null;
        }
    }

    @Override
    public TestResponseDTO getTestContextForStudent(String courseId, String assessmentId, String studentId) {
        try {
            TestResponseDTO testContext = testProxyClient.getTestByStudentAndCourse(courseId, assessmentId, studentId);
            log.info("Fetched test context for course: {}, assessment: {}, student: {}", 
                    courseId, assessmentId, studentId);
            return testContext;
        } catch (Exception e) {
            log.warn("Failed to fetch test context for course: {}, assessment: {}, student: {}. Error: {}",
                    courseId, assessmentId, studentId, e.getMessage());
            return null;
        }
    }

    @Override
    public Optional<QuestionDTO> findQuestion(TestResponseDTO testContext, Long questionId) {
        if (testContext == null || testContext.getQuestionList() == null) {
            log.debug("Test context or question list is null, cannot find question: {}", questionId);
            return Optional.empty();
        }
        
        return testContext.getQuestionList().stream()
                .filter(q -> q.getQuestionId() != null && q.getQuestionId().equals(questionId))
                .findFirst();
    }
}
