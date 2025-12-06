package com.example.demo.services.hint;

import java.util.Map;

import com.example.demo.dto.QuestionDTO;
import com.example.demo.dto.TestResponseDTO;
import com.example.demo.proxy.TestProxyClient;
import com.example.demo.services.prompt.impl.HintGenerationBuildPrompt;
import com.example.demo.services.prompt.context.HintPromptContext;
import com.example.demo.services.task.AITask;
import org.springframework.stereotype.Component;

import com.example.demo.dto.request.AIHintRequest;
import com.example.demo.dto.AIResponse;
import com.example.demo.llm.LLMClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AI Task for generating hints.
 * Follows Single Responsibility Principle - only handles hint generation logic.
 * Uses BuildPrompt for prompt construction via dependency injection.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class HintGenerationTask implements AITask<AIHintRequest> {

    public static final String TASK_TYPE = "HINT_GENERATION";

    private final LLMClient llmClient;
    private final TestProxyClient testProxyClient;
    private final HintGenerationBuildPrompt buildPrompt;

    @Override
    public AIResponse execute(AIHintRequest request) {
        log.info("Executing hint generation task for course: {}, assessment: {}, question: {}",
                request.getCourseId(), request.getAssessmentId(), request.getQuestionId());

        // Fetch test context from external API
        TestResponseDTO testContext = fetchTestContext(request.getCourseId(), request.getAssessmentId());
        
        // Find the specific question
        QuestionDTO targetQuestion = findQuestion(testContext, request.getQuestionId());

        HintPromptContext context = HintPromptContext.builder()
                .request(request)
                .testContext(testContext)
                .targetQuestion(targetQuestion)
                .build();

        String prompt = buildPrompt.buildPrompt(context);
        log.debug("Generated prompt type: {}", buildPrompt.getPromptType());
        System.out.println("Generated Prompt:\n" + prompt);
        String result = llmClient.chat(prompt);

        return AIResponse.builder()
                .result(result)
                .metadata(Map.of(
                        "taskType", TASK_TYPE,
                        "promptType", buildPrompt.getPromptType().getValue(),
                        "courseId", request.getCourseId() != null ? request.getCourseId() : "unknown",
                        "assessmentId", request.getAssessmentId() != null ? request.getAssessmentId() : "unknown",
                        "questionId", request.getQuestionId() != null ? request.getQuestionId() : "unknown"
                ))
                .build();
    }

    @Override
    public String getTaskType() {
        return TASK_TYPE;
    }

    private TestResponseDTO fetchTestContext(String courseId, String assessmentId) {
        try {
            return testProxyClient.getTestByCourseAndAssessment(courseId, assessmentId);
        } catch (Exception e) {
            log.warn("Failed to fetch test context for course: {}, assessment: {}. Error: {}",
                    courseId, assessmentId, e.getMessage());
            return null;
        }
    }

    private QuestionDTO findQuestion(TestResponseDTO testContext, Long questionId) {
        if (testContext == null || testContext.getQuestionList() == null) {
            return null;
        }
        return testContext.getQuestionList().stream()
                .filter(q -> q.getQuestionId() != null && q.getQuestionId().equals(questionId))
                .findFirst()
                .orElse(null);
    }
}
