package com.example.demo.services.prompt.impl;

import com.example.demo.dto.material.ChapterDTO;
import com.example.demo.dto.material.MaterialDTO;
import com.example.demo.dto.request.AIMaterialRequest;
import com.example.demo.services.prompt.BuildPrompt;
import com.example.demo.services.prompt.PromptType;
import com.example.demo.services.prompt.context.MaterialRecommendationPromptContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * BuildPrompt implementation for generating material recommendation prompts.
 * Follows Single Responsibility Principle - only handles material recommendation prompt construction.
 */
@Component
public class MaterialRecommendationBuildPrompt implements BuildPrompt<MaterialRecommendationPromptContext> {

    @Override
    public String buildPrompt(MaterialRecommendationPromptContext context) {
        AIMaterialRequest request = context.getRequest();
        List<ChapterDTO> chapters = context.getChapters();
        List<MaterialDTO> materials = context.getMaterials();

        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an intelligent tutoring system. Based on the student's request and available course materials, recommend the most suitable learning materials.\n\n");

        // Add student context
        if (request != null) {
            prompt.append("=== STUDENT REQUEST ===\n");
            prompt.append("Student ID: ").append(request.getStudentId()).append("\n");
            prompt.append("Course ID: ").append(request.getCourseId()).append("\n");
            
            if (request.getStudentPrompt() != null && !request.getStudentPrompt().isEmpty()) {
                prompt.append("Student's Query: ").append(request.getStudentPrompt()).append("\n");
            }
            if (request.getPreferredDifficulty() != null) {
                prompt.append("Preferred Difficulty: ").append(request.getPreferredDifficulty()).append("\n");
            }
            if (request.getPreferredType() != null) {
                prompt.append("Preferred Material Type: ").append(request.getPreferredType()).append("\n");
            }
            prompt.append("\n");
        }

        // Add available chapters
        if (chapters != null && !chapters.isEmpty()) {
            prompt.append("=== AVAILABLE CHAPTERS ===\n");
            for (int i = 0; i < chapters.size(); i++) {
                ChapterDTO chapter = chapters.get(i);
                prompt.append(i + 1).append(". ").append(chapter.getTitle());
                if (chapter.getDifficulty() != null) {
                    prompt.append(" (Difficulty: ").append(chapter.getDifficulty()).append(")");
                }
                prompt.append("\n");
            }
            prompt.append("\n");
        }

        // Add available materials
        if (materials != null && !materials.isEmpty()) {
            prompt.append("=== AVAILABLE MATERIALS ===\n");
            for (int i = 0; i < materials.size(); i++) {
                MaterialDTO material = materials.get(i);
                prompt.append(i + 1).append(". ").append(material.getTitle()).append("\n");
                prompt.append("   Type: ").append(material.getType()).append("\n");
                if (material.getMetadata() != null) {
                    prompt.append("   Info: ").append(material.getMetadata()).append("\n");
                }
            }
            prompt.append("\n");
        }

        // Instructions for recommendations
        prompt.append("=== INSTRUCTIONS ===\n");
        prompt.append("Based on the student's query and the available materials, please provide personalized recommendations that:\n");
        prompt.append("1. Directly address the student's learning needs or query\n");
        prompt.append("2. Prioritize materials matching their preferred difficulty and type (if specified)\n");
        prompt.append("3. Suggest a logical learning path through the materials\n");
        prompt.append("4. Explain why each recommended material is relevant\n");
        prompt.append("5. Consider progression from easier to more challenging content\n");
        prompt.append("6. Include a mix of material types (TEXT, VIDEO, INTERACTIVE) for varied learning\n\n");
        prompt.append("Format your response in a clear, structured manner with numbered recommendations.\n");

        return prompt.toString();
    }

    @Override
    public PromptType getPromptType() {
        return PromptType.MATERIAL_RECOMMENDATION;
    }
}
