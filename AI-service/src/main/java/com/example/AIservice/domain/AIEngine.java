package com.example.AIservice.domain;

import java.util.List;

/**
 * Strategy Pattern: AIEngine interface
 * Defines the contract for AI algorithms that generate feedback and learning recommendations.
 * This allows different implementations (SimpleRuleBasedAIEngine, GPTBasedAIEngine, etc.)
 * to be swapped without changing the calling code.
 *
 * LSP (Liskov Substitution Principle):
 * Any implementation of AIEngine can be substituted wherever AIEngine is used.
 */
public interface AIEngine {

    /**
     * Generates personalized feedback based on student's submission context
     * @param ctx The submission context containing question and answer details
     * @return AIFeedback object with feedback text and hints
     */
    AIFeedback generateFeedback(SubmissionContext ctx);

    /**
     * Suggests next learning steps for a student
     * @param studentId The ID of the student
     * @return List of learning recommendations
     */
    List<LearningRecommendation> suggestNextSteps(Long studentId);
}

