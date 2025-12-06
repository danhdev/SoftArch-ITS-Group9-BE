package com.example.demo.services.material;

import java.util.Map;

import com.example.demo.services.task.AITask;
import org.springframework.stereotype.Component;

import com.example.demo.dto.request.AIFeedbackRequest;
import com.example.demo.dto.AIResponse;
import com.example.demo.llm.LLMClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AI Task for generating material recommendations.
 * Follows Single Responsibility Principle - only handles material recommendation logic.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MaterialRecommendationTask implements AITask<AIFeedbackRequest> {

    public static final String TASK_TYPE = "MATERIAL_RECOMMENDATION";

    private final LLMClient llmClient;

    @Override
    public AIResponse execute(AIFeedbackRequest request) {
        log.info("Executing material recommendation task for student: {}", request.getStudentId());

        String prompt = buildPrompt(request);
        String result = llmClient.chat(prompt);

        return AIResponse.builder()
                .result(result)
                .metadata(Map.of(
                        "taskType", TASK_TYPE,
                        "studentId", request.getStudentId() != null ? request.getStudentId() : "unknown"
                ))
                .build();
    }

    @Override
    public String getTaskType() {
        return TASK_TYPE;
    }

    private String buildPrompt(AIFeedbackRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Based on the following context, recommend learning materials:\n\n");
//        prompt.append("User Input: ").append(request.getUserInput()).append("\n");
//
//        if (request.getContextData() != null) {
//            prompt.append("Context: ").append(request.getContextData()).append("\n");
//        }

        prompt.append("\nProvide specific, actionable material recommendations.");
        return prompt.toString();
    }
}
