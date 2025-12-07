package com.example.demo.services.feedback;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.demo.dto.AIResponse;
import com.example.demo.dto.TestResponseDTO;
import com.example.demo.dto.request.AIFeedbackRequest;
import com.example.demo.llm.LLMClient;
import com.example.demo.services.dataprovider.FeedbackDataProvider;
import com.example.demo.services.dataprovider.TestDataProvider;
import com.example.demo.services.prompt.context.FeedbackPromptContext;
import com.example.demo.services.prompt.impl.TestFeedbackBuildPrompt;
import com.example.demo.services.task.AITask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AI Task for generating feedback on student submissions.
 * Follows Single Responsibility Principle - handles feedback generation logic and data gathering.
 * Follows Dependency Inversion Principle - depends on abstractions (interfaces).
 * Uses BuildPrompt for prompt construction via dependency injection.
 * Uses DataProviders to gather necessary data for feedback generation.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TestFeedbackGenerationTask implements AITask<AIFeedbackRequest> {

    public static final String TASK_TYPE = "FEEDBACK_GENERATION";

    private final LLMClient llmClient;
    private final TestFeedbackBuildPrompt buildPrompt;

    // DataProviders for SOLID compliance - separate data access concerns
    private final TestDataProvider testDataProvider;
    private final FeedbackDataProvider feedbackDataProvider;

    @Override
    public AIResponse execute(AIFeedbackRequest request) {
        log.info("Executing feedback generation task for student: {}, assessment: {}",
                request.getStudentId(), request.getAssessmentId());

        // Delegate test context fetching to TestDataProvider (SRP compliance)
        TestResponseDTO testContext = testDataProvider.getTestContextForStudent(
                request.getCourseId(),
                request.getAssessmentId(),
                request.getStudentId()
        );

        if (testContext != null) {
            log.info("Fetched test context: {} - {}", testContext.getTitle(), testContext.getDescription());
        }

        // Build context with test data
        FeedbackPromptContext context = FeedbackPromptContext.builder()
                .request(request)
                .testContext(testContext)
                .build();

        String prompt = buildPrompt.buildPrompt(context);
        log.debug("Generated prompt type: {}", buildPrompt.getPromptType());

        // Generate feedback using LLM
        String result = llmClient.chat(prompt);

        AIResponse response = AIResponse.builder()
                .result(result)
                .metadata(Map.of(
                        "taskType", TASK_TYPE,
                        "promptType", buildPrompt.getPromptType().getValue(),
                        "studentId", request.getStudentId() != null ? request.getStudentId() : "unknown",
                        "assessmentId", request.getAssessmentId() != null ? request.getAssessmentId() : "unknown",
                        "courseId", request.getCourseId() != null ? request.getCourseId() : "unknown",
                        "testTitle", testContext != null ? testContext.getTitle() : "unknown"
                ))
                .build();

        // Delegate saving to FeedbackDataProvider (SRP compliance)
        feedbackDataProvider.saveFeedback(
                request.getStudentId(),
                request.getCourseId(),
                request.getAssessmentId(),
                response.getResult()
        );

        return response;
    }

    @Override
    public String getTaskType() {
        return TASK_TYPE;
    }
}
