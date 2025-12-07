package com.example.demo.services.prompt.impl;

import com.example.demo.dto.QuestionDTO;
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
        QuestionDTO targetQuestion = context.getTargetQuestion();
        var testContext = context.getTestContext();

        // Determine hint level based on previous hints count
        int previousHintCount = context.getPreviousHints() != null ? context.getPreviousHints().size() : 0;
        String hintLevel;

        if (previousHintCount == 0) {
            hintLevel = "general, only suggesting direction of thinking";
        } else if (previousHintCount == 1) {
            hintLevel = "clearer, pointing out the solution method";
        } else {
            hintLevel = "more detailed, step-by-step guidance";
        }

        // Build previous hints text
        StringBuilder previousHintsText = new StringBuilder();
        if (context.getPreviousHints() != null && !context.getPreviousHints().isEmpty()) {
            previousHintsText.append("\n\nPrevious hints provided to the student:\n");
            for (int i = 0; i < context.getPreviousHints().size(); i++) {
                previousHintsText.append(String.format("%d. %s\n", i + 1, context.getPreviousHints().get(i)));
            }
            previousHintsText.append("\nThe student still hasn't found the answer, provide a new hint based on the previous hints.");
        }

        // Build test information text
        StringBuilder testInfoText = new StringBuilder();
        if (testContext != null) {
            testInfoText.append("\n\nTEST INFORMATION:\n");
            if (testContext.getTitle() != null && !testContext.getTitle().isEmpty()) {
                testInfoText.append(String.format("- Test title: %s\n", testContext.getTitle()));
            }
            if (testContext.getDescription() != null && !testContext.getDescription().isEmpty()) {
                testInfoText.append(String.format("- Test description: %s\n", testContext.getDescription()));
            }
            if (testContext.getDateTaken() != null && !testContext.getDateTaken().isEmpty()) {
                testInfoText.append(String.format("- Date Taken: %s\n", testContext.getDateTaken()));
            }
            if (testContext.getTimeStart() != null && !testContext.getTimeStart().isEmpty()) {
                testInfoText.append(String.format("- Time Start: %s\n", testContext.getTimeStart()));
            }
            if (testContext.getTimeLimit() != null && !testContext.getTimeLimit().isEmpty()) {
                testInfoText.append(String.format("- Time Limit: %s\n", testContext.getTimeLimit()));
            }
        }

        // Build materials text
        StringBuilder materialsText = new StringBuilder();
        if (context.getMaterials() != null && !context.getMaterials().isEmpty()) {
            materialsText.append("\n\nRELATED COURSE MATERIALS:\n");
            for (int i = 0; i < context.getMaterials().size(); i++) {
                var material = context.getMaterials().get(i);
                materialsText.append(String.format("%d. ", i + 1));

                if (material.getTitle() != null && !material.getTitle().isEmpty()) {
                    materialsText.append(String.format("Title: %s", material.getTitle()));
                }

                if (material.getType() != null && !material.getType().isEmpty()) {
                    materialsText.append(String.format(" (Type: %s)", material.getType()));
                }

                materialsText.append("\n");

                if (material.getContentOrUrl() != null && !material.getContentOrUrl().isEmpty()) {
                    String content = material.getContentOrUrl();
                    materialsText.append(String.format("   Content: %s\n", content));
                }

                if (material.getMetadata() != null && !material.getMetadata().isEmpty()) {
                    materialsText.append(String.format("   Metadata: %s\n", material.getMetadata()));
                }
            }
            materialsText.append("\nPlease refer to the materials above to provide hints appropriate to the course content.");
        }

        // Get question text, correct answer, and options
        String questionText = targetQuestion != null ? targetQuestion.getQuestionText() : "(Question details not available)";
        String correctAnswer = targetQuestion != null && targetQuestion.getCorrectAnswer() != null
                ? targetQuestion.getCorrectAnswer()
                : "Not provided";

        // Build question options text
        StringBuilder optionsText = new StringBuilder();
        if (targetQuestion != null && targetQuestion.getOptions() != null && !targetQuestion.getOptions().isEmpty()) {
            optionsText.append("\n- Options:\n");
            for (int i = 0; i < targetQuestion.getOptions().size(); i++) {
                optionsText.append(String.format("  %c) %s\n", (char) ('A' + i), targetQuestion.getOptions().get(i)));
            }
        }

        // Build the complete prompt
        return String.format("""
                You are a friendly AI tutor in an Intelligent Tutoring System.
                The student is struggling with a question and needs a hint.%s
                
                Information related to the question:
                - Subject: %s
                - Correct Answer: %s
                - Question: %s%s%s%s
                
                Requirements:
                - Provide a hint at the level: %s
                - DO NOT reveal the correct answer directly
                - The hint should help the student find the answer themselves
                - If course materials are available, base your hint on those materials
                - If this is the first hint, keep it general and not too specific
                - If there are previous hints, provide a new hint that doesn't repeat and is more detailed
                - Use a friendly tone with appropriate emojis
                - Return only the hint content (no JSON format needed)
                
                Hint:
                """,
                testInfoText,
                context.getSubject() != null ? context.getSubject() : "Not specified",
                correctAnswer,
                questionText,
                optionsText,
                previousHintsText,
                materialsText,
                hintLevel);
    }

    @Override
    public PromptType getPromptType() {
        return PromptType.HINT_GENERATION;
    }
}
