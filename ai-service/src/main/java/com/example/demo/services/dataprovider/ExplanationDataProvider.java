package com.example.demo.services.dataprovider;

import com.example.demo.models.AIExplanation;

import java.util.List;

/**
 * Interface for explanation data persistence operations.
 * Follows Interface Segregation Principle (ISP) - provides only explanation-specific data operations.
 * Follows Dependency Inversion Principle (DIP) - high-level modules depend on this abstraction.
 */
public interface ExplanationDataProvider {

    /**
     * Fetch previous explanations for a student and material.
     *
     * @param studentId  the student identifier
     * @param materialId the material identifier
     * @return list of explanation entities
     */
    List<AIExplanation> getPreviousExplanations(Long studentId, Long materialId);

    /**
     * Extract previous questions from explanations.
     *
     * @param explanations list of explanation entities
     * @return list of question texts
     */
    List<String> extractQuestions(List<AIExplanation> explanations);

    /**
     * Extract previous answers/explanations from explanations.
     *
     * @param explanations list of explanation entities
     * @return list of explanation texts
     */
    List<String> extractAnswers(List<AIExplanation> explanations);

    /**
     * Save an explanation to the database.
     *
     * @param studentId       the student identifier
     * @param materialId      the material identifier
     * @param studentQuestion the student's question
     * @param explanation     the generated explanation
     * @return the saved explanation entity or null if save failed
     */
    AIExplanation saveExplanation(Long studentId, Long materialId, String studentQuestion, String explanation);

    /**
     * Get explanation history as entities.
     *
     * @param studentId  the student identifier
     * @param materialId the material identifier
     * @return list of explanation entities
     */
    List<AIExplanation> getExplanationHistory(Long studentId, Long materialId);
}
