package com.example.demo.services.material;

import java.util.Map;

import com.example.demo.services.prompt.impl.MaterialRecommendationBuildPrompt;
import com.example.demo.services.prompt.context.MaterialRecommendationPromptContext;
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
 * Uses BuildPrompt for prompt construction via dependency injection.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MaterialRecommendationTask implements AITask<AIFeedbackRequest> {

    public static final String TASK_TYPE = "MATERIAL_RECOMMENDATION";

    private final LLMClient llmClient;
    private final MaterialRecommendationBuildPrompt buildPrompt;

    @Override
    public AIResponse execute(AIFeedbackRequest request) {
        log.info("Executing material recommendation task for student: {}", request.getStudentId());

        MaterialRecommendationPromptContext context = MaterialRecommendationPromptContext.builder()
                .request(request)
                .build();

        String prompt = buildPrompt.buildPrompt(context);
        log.debug("Generated prompt type: {}", buildPrompt.getPromptType());
        String result = llmClient.chat(prompt);

        return AIResponse.builder()
                .result(result)
                .metadata(Map.of(
                        "taskType", TASK_TYPE,
                        "promptType", buildPrompt.getPromptType().getValue(),
                        "studentId", request.getStudentId() != null ? request.getStudentId() : "unknown"
                ))
                .build();
    }

    @Override
    public String getTaskType() {
        return TASK_TYPE;
    }
}
