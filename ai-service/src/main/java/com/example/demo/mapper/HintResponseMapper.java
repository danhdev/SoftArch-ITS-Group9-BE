package com.example.demo.mapper;

import com.example.demo.dto.AIResponse;
import com.example.demo.dto.response.AIGenerationResponseDTO;
import com.example.demo.dto.response.HintResponseDTO;
import com.example.demo.models.AIHint;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapper class for HintController response DTOs.
 * Follows Single Responsibility Principle - handles only mapping for hint-related responses.
 */
@Component
public class HintResponseMapper {
    
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
     * Maps AIHint entity to HintResponseDTO.
     *
     * @param hint      the hint entity
     * @param hintCount the sequence number of this hint
     * @return the mapped response DTO
     */
    public HintResponseDTO toResponseDTO(AIHint hint, int hintCount) {
        if (hint == null) {
            return null;
        }
        return HintResponseDTO.builder()
                .hintId(hint.getId())
                .questionId(hint.getQuestionId())
                .studentId(hint.getStudentId())
                .hint(hint.getHint())
                .hintCount(hintCount)
                .createdAt(hint.getCreatedAt() != null 
                        ? hint.getCreatedAt().format(DATE_FORMATTER) 
                        : null)
                .build();
    }
    
    /**
     * Maps a list of AIHint entities to HintResponseDTOs with sequential hint counts.
     *
     * @param hints the list of hint entities
     * @return the list of mapped response DTOs with sequential hint counts
     */
    public List<HintResponseDTO> toResponseDTOList(List<AIHint> hints) {
        if (hints == null) {
            return List.of();
        }
        
        List<HintResponseDTO> result = new ArrayList<>();
        for (int i = 0; i < hints.size(); i++) {
            result.add(toResponseDTO(hints.get(i), i + 1));
        }
        return result;
    }
}
