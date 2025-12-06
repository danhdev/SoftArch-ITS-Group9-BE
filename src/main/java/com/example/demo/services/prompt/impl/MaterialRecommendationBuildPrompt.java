package com.example.demo.services.prompt.impl;

import com.example.demo.dto.request.AIFeedbackRequest;
import com.example.demo.services.prompt.BuildPrompt;
import com.example.demo.services.prompt.PromptType;
import com.example.demo.services.prompt.context.MaterialRecommendationPromptContext;
import org.springframework.stereotype.Component;

/**
 * BuildPrompt implementation for generating material recommendation prompts.
 * Follows Single Responsibility Principle - only handles material recommendation prompt construction.
 */
@Component
public class MaterialRecommendationBuildPrompt implements BuildPrompt<MaterialRecommendationPromptContext> {

    @Override
    public String buildPrompt(MaterialRecommendationPromptContext context) {
        AIFeedbackRequest request = context.getRequest();

        StringBuilder prompt = new StringBuilder();
        prompt.append("Based on the following context, recommend learning materials:\n\n");

        // Add context information if available
        if (request != null) {
            prompt.append("=== CONTEXT ===\n");
            if (request.getStudentId() != null) {
                prompt.append("Student ID: ").append(request.getStudentId()).append("\n");
            }
            if (request.getCourseId() != null) {
                prompt.append("Course ID: ").append(request.getCourseId()).append("\n");
            }
            if (request.getAssessmentId() != null) {
                prompt.append("Assessment ID: ").append(request.getAssessmentId()).append("\n");
            }
            prompt.append("\n");
        }

        // Instructions for recommendations
        prompt.append("=== INSTRUCTIONS ===\n");
        prompt.append("Please provide specific, actionable material recommendations that:\n");
        prompt.append("1. Are relevant to the student's current learning context\n");
        prompt.append("2. Include various types of resources (videos, articles, exercises, etc.)\n");
        prompt.append("3. Are organized by difficulty or topic\n");
        prompt.append("4. Provide brief descriptions of why each resource is helpful\n");
        prompt.append("5. Consider different learning styles\n");

        return prompt.toString();
    }

    @Override
    public PromptType getPromptType() {
        return PromptType.MATERIAL_RECOMMENDATION;
    }
}
