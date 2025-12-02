package com.example.AIservice.domain;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * SimpleRuleBasedAIEngine: A basic implementation of AIEngine using simple rules
 * This implementation uses rule-based logic to generate feedback and recommendations.
 * It can be replaced with more sophisticated implementations (GPTBasedAIEngine, etc.)
 * without changing the code that uses the AIEngine interface.
 */
@Component
public class SimpleRuleBasedAIEngine implements AIEngine {

    @Override
    public AIFeedback generateFeedback(SubmissionContext ctx) {
        if (ctx == null) {
            throw new IllegalArgumentException("SubmissionContext cannot be null");
        }

        String feedbackText = generateFeedbackText(ctx);
        String hint = generateHint(ctx);

        return AIFeedback.builder()
                .studentId(ctx.getStudentId())
                .questionId(ctx.getQuestionId())
                .feedbackText(feedbackText)
                .hint(hint)
                .build();
    }

    @Override
    public List<LearningRecommendation> suggestNextSteps(Long studentId) {
        if (studentId == null) {
            throw new IllegalArgumentException("StudentId cannot be null");
        }

        List<LearningRecommendation> recommendations = new ArrayList<>();

        // Simple rule-based recommendation
        // In a real implementation, this would analyze student's history and performance
        recommendations.add(LearningRecommendation.builder()
                .studentId(studentId)
                .nextTopic("Review fundamental concepts")
                .explanation("Based on your recent performance, reviewing basic concepts will help strengthen your foundation.")
                .build());

        recommendations.add(LearningRecommendation.builder()
                .studentId(studentId)
                .nextTopic("Practice similar problems")
                .explanation("Try solving more problems of similar difficulty to improve your understanding.")
                .build());

        return recommendations;
    }

    /**
     * Generates feedback text based on student's answer correctness
     */
    private String generateFeedbackText(SubmissionContext ctx) {
        if (ctx.getStudentAnswer() == null || ctx.getCorrectAnswer() == null) {
            return "Unable to generate feedback: missing answer information.";
        }

        String studentAnswer = ctx.getStudentAnswer().trim().toLowerCase();
        String correctAnswer = ctx.getCorrectAnswer().trim().toLowerCase();

        if (studentAnswer.equals(correctAnswer)) {
            return generatePositiveFeedback(ctx);
        } else if (studentAnswer.contains(correctAnswer) || correctAnswer.contains(studentAnswer)) {
            return generatePartialFeedback(ctx);
        } else {
            return generateNegativeFeedback(ctx);
        }
    }

    /**
     * Generates a hint based on the question context
     */
    private String generateHint(SubmissionContext ctx) {
        if (ctx.getTopic() != null && !ctx.getTopic().isEmpty()) {
            return "Review the key concepts in: " + ctx.getTopic() +
                   ". Pay attention to the relationship between the question and the correct answer.";
        }
        return "Try breaking down the problem into smaller parts and review the relevant concepts.";
    }

    private String generatePositiveFeedback(SubmissionContext ctx) {
        return "Excellent work! Your answer is correct. " +
               "You demonstrated a good understanding of " +
               (ctx.getTopic() != null ? ctx.getTopic() : "the concept") + ".";
    }

    private String generatePartialFeedback(SubmissionContext ctx) {
        return "Good effort! Your answer is partially correct. " +
               "You're on the right track, but there are some details that need attention. " +
               "Review the question carefully and consider all aspects of " +
               (ctx.getTopic() != null ? ctx.getTopic() : "the problem") + ".";
    }

    private String generateNegativeFeedback(SubmissionContext ctx) {
        return "Your answer needs improvement. " +
               "The correct answer is: " + ctx.getCorrectAnswer() + ". " +
               "Take time to understand the concepts related to " +
               (ctx.getTopic() != null ? ctx.getTopic() : "this question") +
               " and try again.";
    }
}

