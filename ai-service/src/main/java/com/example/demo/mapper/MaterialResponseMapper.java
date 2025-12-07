package com.example.demo.mapper;

import com.example.demo.dto.AIResponse;
import com.example.demo.dto.response.AIGenerationResponseDTO;
import com.example.demo.dto.response.ExplanationResponseDTO;
import com.example.demo.models.AIExplanation;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for MaterialController response DTOs.
 * Follows Single Responsibility Principle - handles only mapping for material-related responses.
 */
@Component
public class MaterialResponseMapper {
    
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
     * Maps AIExplanation entity to ExplanationResponseDTO.
     *
     * @param explanation the explanation entity
     * @return the mapped response DTO
     */
    public ExplanationResponseDTO toExplanationResponseDTO(AIExplanation explanation) {
        if (explanation == null) {
            return null;
        }
        return ExplanationResponseDTO.builder()
                .explanationId(explanation.getId())
                .studentId(explanation.getStudentId())
                .materialId(explanation.getMaterialId())
                .studentQuestion(explanation.getStudentQuestion())
                .explanation(explanation.getExplanation())
                .createdAt(explanation.getCreatedAt() != null 
                        ? explanation.getCreatedAt().format(DATE_FORMATTER) 
                        : null)
                .build();
    }
    
    /**
     * Maps a list of AIExplanation entities to ExplanationResponseDTOs.
     *
     * @param explanations the list of explanation entities
     * @return the list of mapped response DTOs
     */
    public List<ExplanationResponseDTO> toExplanationResponseDTOList(List<AIExplanation> explanations) {
        if (explanations == null) {
            return List.of();
        }
        return explanations.stream()
                .map(this::toExplanationResponseDTO)
                .collect(Collectors.toList());
    }
}
