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
 * AI Task for generating material explanations.
 * Follows Single Responsibility Principle - only handles material explanation logic.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MaterialExplanationTask implements AITask<AIFeedbackRequest> {

    public static final String TASK_TYPE = "MATERIAL_EXPLANATION";

    private final LLMClient llmClient;

    @Override
    public AIResponse execute(AIFeedbackRequest request) {
        log.info("Executing material explanation task");

        String prompt = buildPrompt(request);
        String result = llmClient.chat(prompt);

        return AIResponse.builder()
                .result(result)
                .metadata(Map.of(
                        "taskType", TASK_TYPE
                ))
                .build();
    }

    @Override
    public String getTaskType() {
        return TASK_TYPE;
    }

    private String buildPrompt(AIFeedbackRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Please provide a detailed explanation for the following topic:\n\n");
//        prompt.append("Topic: ").append(request.getUserInput()).append("\n");
//
//        if (request.getContextData() != null) {
//            prompt.append("Additional Context: ").append(request.getContextData()).append("\n");
//        }
//
//        if (request.getQuestionList() != null && !request.getQuestionList().isEmpty()) {
//            prompt.append("Related Questions: ").append(String.join(", ", request.getQuestionList())).append("\n");
//        }

        prompt.append("\nProvide a clear, educational explanation suitable for a student.");
        return prompt.toString();
    }
}
