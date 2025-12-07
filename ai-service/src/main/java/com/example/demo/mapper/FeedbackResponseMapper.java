package com.example.demo.mapper;

import com.example.demo.dto.AIResponse;
import com.example.demo.dto.response.AIGenerationResponseDTO;
import com.example.demo.dto.response.FeedbackResponseDTO;
import com.example.demo.models.FeedbackRecord;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for FeedbackController response DTOs.
 * Follows Single Responsibility Principle - handles only mapping for feedback-related responses.
 */
@Component
public class FeedbackResponseMapper {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Maps AIResponse to AIGenerationResponseDTO.
     *
     * @param response the AI response from service
     * @return the mapped response DTO
     */
    public AIGenerationResponseDTO toGenerationResponse(AIResponse response) {
        if (response == null) {
            return null;
        }
        return AIGenerationResponseDTO.builder()
                .result(response.getResult())
                .metadata(response.getMetadata())
                .build();
    }
    
    /**
     * Maps FeedbackRecord entity to FeedbackResponseDTO.
     *
     * @param record the feedback record entity
     * @return the mapped response DTO
     */
    public FeedbackResponseDTO toResponseDTO(FeedbackRecord record) {
        if (record == null) {
            return null;
        }
        return FeedbackResponseDTO.builder()
                .id(record.getId())
                .studentId(record.getStudentId())
                .courseId(record.getCourseId())
                .assessmentId(record.getAssessmentId())
                .feedbackText(record.getFeedbackText())
                .createdAt(record.getCreatedAt() != null 
                        ? record.getCreatedAt().format(DATE_FORMATTER) 
                        : null)
                .build();
    }
    
    /**
     * Maps a list of FeedbackRecord entities to FeedbackResponseDTOs.
     *
     * @param records the list of feedback record entities
     * @return the list of mapped response DTOs
     */
    public List<FeedbackResponseDTO> toResponseDTOList(List<FeedbackRecord> records) {
        if (records == null) {
            return List.of();
        }
        return records.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}
