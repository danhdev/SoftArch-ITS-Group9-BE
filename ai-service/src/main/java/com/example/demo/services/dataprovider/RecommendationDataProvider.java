package com.example.demo.services.dataprovider;

import com.example.demo.dto.request.AIMaterialRequest;
import com.example.demo.dto.AIResponse;
import com.example.demo.models.MaterialRecommendationRecord;

import java.util.List;
import java.util.Optional;

/**
 * Interface for recommendation data persistence operations.
 * Follows Interface Segregation Principle (ISP) - provides only recommendation-specific data operations.
 * Follows Dependency Inversion Principle (DIP) - high-level modules depend on this abstraction.
 */
public interface RecommendationDataProvider {

    /**
     * Save a recommendation record to the database.
     *
     * @param request  the original material request
     * @param response the AI response containing the recommendation
     * @return the saved recommendation record or null if save failed
     */
    MaterialRecommendationRecord saveRecommendation(AIMaterialRequest request, AIResponse response);

    /**
     * Get recommendation history for a student.
     *
     * @param studentId the student identifier
     * @return list of recommendation records
     */
    List<MaterialRecommendationRecord> getRecommendationHistory(String studentId);

    /**
     * Get recommendation history for a student and course.
     *
     * @param studentId the student identifier
     * @param courseId  the course identifier
     * @return list of recommendation records
     */
    List<MaterialRecommendationRecord> getRecommendationHistory(String studentId, String courseId);

    /**
     * Get the most recent recommendation for a student and course.
     *
     * @param studentId the student identifier
     * @param courseId  the course identifier
     * @return the most recent recommendation or empty
     */
    Optional<MaterialRecommendationRecord> getLatestRecommendation(String studentId, String courseId);
}
