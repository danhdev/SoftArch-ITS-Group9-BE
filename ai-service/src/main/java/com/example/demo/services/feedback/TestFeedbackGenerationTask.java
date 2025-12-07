package com.example.demo.services.feedback;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.demo.dto.request.AIFeedbackRequest;
import com.example.demo.dto.AIResponse;
import com.example.demo.dto.TestResponseDTO;
import com.example.demo.llm.LLMClient;
import com.example.demo.services.prompt.impl.TestFeedbackBuildPrompt;
import com.example.demo.services.prompt.context.FeedbackPromptContext;
import com.example.demo.services.task.AITask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AI Task for generating feedback on student submissions.
 * Follows Single Responsibility Principle - only handles feedback generation logic.
 * Uses BuildPrompt for prompt construction via dependency injection.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TestFeedbackGenerationTask implements AITask<AIFeedbackRequest> {

    public static final String TASK_TYPE = "FEEDBACK_GENERATION";

    private final LLMClient llmClient;
    private final TestFeedbackBuildPrompt buildPrompt;

    @Override
    public AIResponse execute(AIFeedbackRequest request) {
        log.info("Executing feedback generation task for student: {}, assessment: {}",
                request.getStudentId(), request.getAssessmentId());

        FeedbackPromptContext context = FeedbackPromptContext.builder()
                .request(request)
                .testContext(null)
                .build();

        String prompt = buildPrompt.buildPrompt(context);
        log.debug("Generated prompt type: {}", buildPrompt.getPromptType());
        String result = llmClient.chat(prompt);

        return AIResponse.builder()
                .result(result)
                .metadata(Map.of(
                        "taskType", TASK_TYPE,
                        "promptType", buildPrompt.getPromptType().getValue(),
                        "studentId", request.getStudentId() != null ? request.getStudentId() : "unknown",
                        "assessmentId", request.getAssessmentId() != null ? request.getAssessmentId() : "unknown",
                        "courseId", request.getCourseId() != null ? request.getCourseId() : "unknown"
                ))
                .build();
    }

    /**
     * Execute feedback generation with test context from external API.
     *
     * @param request     the AI request containing student input
     * @param testContext the test/assessment context from external service
     * @return AI response with feedback
     */
    public AIResponse execute(AIFeedbackRequest request, TestResponseDTO testContext) {
        log.info("Executing feedback generation task for student: {}, assessment: {}, test: {}",
                request.getStudentId(), request.getAssessmentId(), 
                testContext != null ? testContext.getTitle() : "unknown");

        FeedbackPromptContext context = FeedbackPromptContext.builder()
                .request(request)
                .testContext(testContext)
                .build();

        String prompt = buildPrompt.buildPrompt(context);
        log.debug("Generated prompt type: {}", buildPrompt.getPromptType());
        String result = llmClient.chat(prompt);

        return AIResponse.builder()
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
    }

    @Override
    public String getTaskType() {
        return TASK_TYPE;
    }
}
