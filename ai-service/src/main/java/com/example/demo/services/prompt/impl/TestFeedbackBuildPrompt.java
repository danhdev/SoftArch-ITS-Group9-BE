package com.example.demo.services.prompt.impl;

import com.example.demo.dto.QuestionDTO;
import com.example.demo.dto.TestResponseDTO;
import com.example.demo.dto.request.AIFeedbackRequest;
import com.example.demo.services.prompt.BuildPrompt;
import com.example.demo.services.prompt.PromptType;
import com.example.demo.services.prompt.context.FeedbackPromptContext;
import org.springframework.stereotype.Component;

/**
 * BuildPrompt implementation for generating test feedback prompts.
 * Follows Single Responsibility Principle - only handles feedback prompt construction.
 */
@Component
public class TestFeedbackBuildPrompt implements BuildPrompt<FeedbackPromptContext> {

    @Override
    public String buildPrompt(FeedbackPromptContext context) {
        AIFeedbackRequest request = context.getRequest();
        TestResponseDTO testContext = context.getTestContext();

        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an intelligent tutoring system providing feedback on a student's test submission.\n\n");

        // Add test context if available
        if (testContext != null) {
            prompt.append("=== TEST INFORMATION ===\n");
            prompt.append("Title: ").append(testContext.getTitle()).append("\n");
            prompt.append("Description: ").append(testContext.getDescription()).append("\n");
            prompt.append("Date Taken: ").append(testContext.getDateTaken()).append("\n");
            prompt.append("Time Limit: ").append(testContext.getTimeLimit()).append("\n\n");

            // Add questions with their details and student answers
            if (testContext.getQuestionList() != null && !testContext.getQuestionList().isEmpty()) {
                prompt.append("=== QUESTIONS, ANSWERS AND STUDENT RESPONSES ===\n");
                int correctCount = 0;
                for (QuestionDTO question : testContext.getQuestionList()) {
                    prompt.append("\nQuestion ").append(question.getQuestionId()).append(": ");
                    prompt.append(question.getQuestionText()).append("\n");
                    prompt.append("Options: ").append(String.join(", ", question.getOptions())).append("\n");
                    prompt.append("Correct Answer: ").append(question.getCorrectAnswer()).append("\n");
                    prompt.append("Student's Answer: ").append(question.getStudentAnswer() != null ? question.getStudentAnswer() : "Not answered").append("\n");

                    // Check if answer is correct
                    boolean isCorrect = question.getCorrectAnswer() != null &&
                            question.getCorrectAnswer().equalsIgnoreCase(question.getStudentAnswer());
                    prompt.append("Result: ").append(isCorrect ? "✓ Correct" : "✗ Incorrect").append("\n");
                    if (isCorrect) correctCount++;
                }
                prompt.append("\n=== SCORE SUMMARY ===\n");
                prompt.append("Total Questions: ").append(testContext.getQuestionList().size()).append("\n");
                prompt.append("Correct Answers: ").append(correctCount).append("\n");
                prompt.append("Score: ").append(String.format("%.1f%%", (correctCount * 100.0 / testContext.getQuestionList().size()))).append("\n\n");
            }
        }

        // Add student information
        prompt.append("=== STUDENT INFORMATION ===\n");
        prompt.append("Student ID: ").append(testContext != null && testContext.getStudentId() != null ? testContext.getStudentId() : request.getStudentId()).append("\n");
        prompt.append("\n");

        // Instructions for feedback generation
        prompt.append("=== INSTRUCTIONS ===\n");
        prompt.append("Please provide comprehensive feedback that includes:\n");
        prompt.append("1. **Overall Performance**: A brief summary of how the student performed\n");
        prompt.append("2. **Correct Answers**: Highlight which questions were answered correctly\n");
        prompt.append("3. **Areas for Improvement**: Identify questions answered incorrectly and explain why\n");
        prompt.append("4. **Detailed Explanations**: For each incorrect answer, explain the correct concept\n");
        prompt.append("5. **Study Recommendations**: Suggest specific topics or resources for improvement\n");
        prompt.append("6. **Encouragement**: Provide constructive and encouraging feedback\n\n");
        prompt.append("Format your response in a clear, structured manner that helps the student learn.\n");

        return prompt.toString();
    }

    @Override
    public PromptType getPromptType() {
        return PromptType.FEEDBACK_GENERATION;
    }
}
