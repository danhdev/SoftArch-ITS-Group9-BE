package com.example.demo.services.prompt.impl;

import com.example.demo.dto.request.AIFeedbackRequest;
import com.example.demo.services.prompt.BuildPrompt;
import com.example.demo.services.prompt.PromptType;
import com.example.demo.services.prompt.context.MaterialExplanationPromptContext;
import org.springframework.stereotype.Component;

/**
 * BuildPrompt implementation for generating material explanation prompts.
 * Follows Single Responsibility Principle - only handles material explanation prompt construction.
 */
@Component
public class MaterialExplanationBuildPrompt implements BuildPrompt<MaterialExplanationPromptContext> {

    @Override
    public String buildPrompt(MaterialExplanationPromptContext context) {
        AIFeedbackRequest request = context.getRequest();

        StringBuilder prompt = new StringBuilder();
        prompt.append("Please provide a detailed explanation for the following topic:\n\n");

        // Add context information if available
        if (request != null) {
            prompt.append("=== CONTEXT ===\n");
            if (request.getCourseId() != null) {
                prompt.append("Course ID: ").append(request.getCourseId()).append("\n");
            }
            if (request.getAssessmentId() != null) {
                prompt.append("Assessment ID: ").append(request.getAssessmentId()).append("\n");
            }
            prompt.append("\n");
        }

        // Instructions for explanation
        prompt.append("=== INSTRUCTIONS ===\n");
        prompt.append("Please provide a clear, educational explanation that:\n");
        prompt.append("1. Breaks down complex concepts into simpler parts\n");
        prompt.append("2. Uses examples where appropriate\n");
        prompt.append("3. Is suitable for a student's learning level\n");
        prompt.append("4. Highlights key points and important concepts\n");
        prompt.append("5. Provides actionable insights for better understanding\n");

        return prompt.toString();
    }

    @Override
    public PromptType getPromptType() {
        return PromptType.MATERIAL_EXPLANATION;
    }
}
