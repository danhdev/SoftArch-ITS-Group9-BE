package com.example.demo.services.prompt.impl;

import com.example.demo.dto.QuestionDTO;
import com.example.demo.dto.TestResponseDTO;
import com.example.demo.dto.request.AIHintRequest;
import com.example.demo.services.prompt.BuildPrompt;
import com.example.demo.services.prompt.PromptType;
import com.example.demo.services.prompt.context.HintPromptContext;
import org.springframework.stereotype.Component;

/**
 * BuildPrompt implementation for generating hint prompts.
 * Follows Single Responsibility Principle - only handles hint prompt construction.
 */
@Component
public class HintGenerationBuildPrompt implements BuildPrompt<HintPromptContext> {

    @Override
    public String buildPrompt(HintPromptContext context) {
        AIHintRequest request = context.getRequest();
        TestResponseDTO testContext = context.getTestContext();
        QuestionDTO targetQuestion = context.getTargetQuestion();

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

    @Override
    public PromptType getPromptType() {
        return PromptType.HINT_GENERATION;
    }
}
