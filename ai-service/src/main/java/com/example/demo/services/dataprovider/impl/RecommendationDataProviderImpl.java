package com.example.demo.services.dataprovider.impl;

import com.example.demo.dto.request.AIMaterialRequest;
import com.example.demo.dto.AIResponse;
import com.example.demo.models.MaterialRecommendationRecord;
import com.example.demo.repository.MaterialRecommendationRepository;
import com.example.demo.services.dataprovider.RecommendationDataProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of RecommendationDataProvider.
 * Follows Single Responsibility Principle - handles only recommendation data persistence operations.
 * Centralizes all recommendation-related database operations.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RecommendationDataProviderImpl implements RecommendationDataProvider {

    private final MaterialRecommendationRepository recommendationRepository;

    @Override
    public MaterialRecommendationRecord saveRecommendation(AIMaterialRequest request, AIResponse response) {
        try {
            MaterialRecommendationRecord record = MaterialRecommendationRecord.builder()
                    .studentId(request.getStudentId())
                    .courseId(request.getCourseId())
                    .recommendationText(response.getResult())
                    .preferredDifficulty(request.getPreferredDifficulty())
                    .preferredType(request.getPreferredType())
                    .createdAt(LocalDateTime.now())
                    .build();

            MaterialRecommendationRecord savedRecord = recommendationRepository.save(record);
            log.info("Saved recommendation record with ID: {} for student: {}", 
                    savedRecord.getId(), request.getStudentId());
            return savedRecord;
        } catch (Exception e) {
            log.error("Error saving recommendation record for student: {}. Error: {}", 
                    request.getStudentId(), e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<MaterialRecommendationRecord> getRecommendationHistory(String studentId) {
        try {
            List<MaterialRecommendationRecord> records = 
                    recommendationRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
            log.info("Fetched {} recommendation records for student: {}", records.size(), studentId);
            return records;
        } catch (Exception e) {
            log.warn("Failed to fetch recommendation history for student: {}. Error: {}", 
                    studentId, e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<MaterialRecommendationRecord> getRecommendationHistory(String studentId, String courseId) {
        try {
            List<MaterialRecommendationRecord> records = 
                    recommendationRepository.findByStudentIdAndCourseIdOrderByCreatedAtDesc(studentId, courseId);
            log.info("Fetched {} recommendation records for student: {}, course: {}", 
                    records.size(), studentId, courseId);
            return records;
        } catch (Exception e) {
            log.warn("Failed to fetch recommendation history for student: {}, course: {}. Error: {}", 
                    studentId, courseId, e.getMessage());
            return List.of();
        }
    }

    @Override
    public Optional<MaterialRecommendationRecord> getLatestRecommendation(String studentId, String courseId) {
        try {
            return recommendationRepository
                    .findFirstByStudentIdAndCourseIdOrderByCreatedAtDesc(studentId, courseId);
        } catch (Exception e) {
            log.warn("Failed to fetch latest recommendation for student: {}, course: {}. Error: {}", 
                    studentId, courseId, e.getMessage());
            return Optional.empty();
        }
    }
}
