package com.example.demo.services.hint;

import java.util.Map;

import com.example.demo.dto.QuestionDTO;
import com.example.demo.dto.TestResponseDTO;
import com.example.demo.proxy.TestProxyClient;
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
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class HintGenerationTask implements AITask<AIHintRequest> {

    public static final String TASK_TYPE = "HINT_GENERATION";

    private final LLMClient llmClient;
    private final TestProxyClient testProxyClient;

    @Override
    public AIResponse execute(AIHintRequest request) {
        log.info("Executing hint generation task for course: {}, assessment: {}, question: {}",
                request.getCourseId(), request.getAssessmentId(), request.getQuestionId());

        // Fetch test context from external API
        TestResponseDTO testContext = fetchTestContext(request.getCourseId(), request.getAssessmentId());
        
        // Find the specific question
        QuestionDTO targetQuestion = findQuestion(testContext, request.getQuestionId());
        
        String prompt = buildPrompt(request, testContext, targetQuestion);
        System.out.println("Generated Prompt:\n" + prompt);
        String result = llmClient.chat(prompt);

        return AIResponse.builder()
                .result(result)
                .metadata(Map.of(
                        "taskType", TASK_TYPE,
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

    private String buildPrompt(AIHintRequest request, TestResponseDTO testContext, QuestionDTO targetQuestion) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an intelligent tutoring system. Generate a helpful hint for the following problem without giving away the answer.\n\n");

        // Add test context if available
        if (testContext != null) {
            prompt.append("=== TEST INFORMATION ===\n");
            prompt.append("Title: ").append(testContext.getTitle()).append("\n");
            prompt.append("Description: ").append(testContext.getDescription()).append("\n\n");
        }

        // Add the specific question
        if (targetQuestion != null) {
            prompt.append("=== QUESTION ===\n");
            prompt.append("Question ID: ").append(targetQuestion.getQuestionId()).append("\n");
            prompt.append("Question: ").append(targetQuestion.getQuestionText()).append("\n");
            if (targetQuestion.getOptions() != null && !targetQuestion.getOptions().isEmpty()) {
                prompt.append("Options: ").append(String.join(", ", targetQuestion.getOptions())).append("\n");
            }
            prompt.append("\n");
        } else {
            prompt.append("=== QUESTION ===\n");
            prompt.append("Question ID: ").append(request.getQuestionId()).append("\n");
            prompt.append("(Question details not available)\n\n");
        }

        // Instructions for hint generation
        prompt.append("=== INSTRUCTIONS ===\n");
        prompt.append("Please provide a helpful hint that:\n");
        prompt.append("1. Guides the student towards understanding the concept\n");
        prompt.append("2. Does NOT reveal the correct answer directly\n");
        prompt.append("3. Encourages critical thinking\n");
        prompt.append("4. Provides a stepping stone or clue to help solve the problem\n");
        prompt.append("5. Is concise and clear\n");

        return prompt.toString();
    }
}
