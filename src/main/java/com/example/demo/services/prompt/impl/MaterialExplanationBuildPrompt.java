package com.example.demo.services.prompt.impl;

import com.example.demo.services.prompt.BuildPrompt;
import com.example.demo.services.prompt.PromptType;
import com.example.demo.services.prompt.context.MaterialExplanationPromptContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * BuildPrompt implementation for generating material explanation prompts.
 * Follows Single Responsibility Principle - only handles material explanation prompt construction.
 */
@Component
public class MaterialExplanationBuildPrompt implements BuildPrompt<MaterialExplanationPromptContext> {

    private static final Logger log = LoggerFactory.getLogger(MaterialExplanationBuildPrompt.class);

    @Override
    public String buildPrompt(MaterialExplanationPromptContext context) {
        // Build material content section
        StringBuilder materialContentText = new StringBuilder();
        if (context.getMaterialContent() != null && !context.getMaterialContent().isEmpty()) {
            materialContentText.append("\n📚 MATERIAL CONTENT:\n");

            if (context.getFileName() != null && !context.getFileName().isEmpty()) {
                materialContentText.append(String.format("File name: %s\n", context.getFileName()));
            }

            if (context.getPages() != null && !context.getPages().isEmpty()) {
                materialContentText.append(String.format("Pages: %s\n", context.getPages()));
            }

            materialContentText.append("\nContent:\n");
            materialContentText.append(context.getMaterialContent());
            materialContentText.append("\n");
        }

        // Build previous Q&A section
        StringBuilder previousQAText = new StringBuilder();
        if (context.getPreviousQuestions() != null && !context.getPreviousQuestions().isEmpty() &&
            context.getPreviousExplanations() != null && !context.getPreviousExplanations().isEmpty()) {

            previousQAText.append("\n\n💬 PREVIOUS QUESTIONS AND EXPLANATIONS:\n");
            int count = Math.min(context.getPreviousQuestions().size(), context.getPreviousExplanations().size());

            for (int i = 0; i < count; i++) {
                previousQAText.append(String.format("\n--- Question %d ---\n", i + 1));
                previousQAText.append(String.format("❓ Question: %s\n", context.getPreviousQuestions().get(i)));
                previousQAText.append(String.format("💡 Explanation: %s\n", context.getPreviousExplanations().get(i)));
            }

            previousQAText.append("\n⚠️ The student still doesn't fully understand this material and has a new question. Please explain from a different perspective or in more detail.");
        }

        // Build complete prompt
        String prompt = String.format("""
                You are a friendly AI tutor in an Intelligent Tutoring System.
                The student is studying a material and has a question that needs explanation.

                %s%s

                ❓ STUDENT'S QUESTION:
                %s

                REQUIREMENTS:
                - Base your explanation on the MATERIAL CONTENT above
                - Explain in a clear way, appropriate for the student's level
                - If there are previous questions, don't repeat old explanations but provide new perspectives
                - You can provide illustrative examples to help the student understand better
                - Encourage the student to think independently and ask follow-up questions
                - Use a friendly tone with appropriate emojis
                - Return ONLY the explanation content (no JSON format needed)
                """,
                materialContentText,
                previousQAText,
                context.getStudentQuestion() != null ? context.getStudentQuestion() : "No question provided");

        // Log the generated prompt
        log.info("========== MATERIAL EXPLANATION PROMPT GENERATED ==========");
        log.info("Student Question: {}", context.getStudentQuestion());
        log.info("Material File: {}", context.getFileName());
        log.info("Previous Questions Count: {}",
                context.getPreviousQuestions() != null ? context.getPreviousQuestions().size() : 0);
        log.info("===========================================================");
        log.info("FULL PROMPT:\n{}", prompt);
        log.info("===========================================================");

        return prompt;
    }

    @Override
    public PromptType getPromptType() {
        return PromptType.MATERIAL_EXPLANATION;
    }
}
