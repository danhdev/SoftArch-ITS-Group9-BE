package com.example.its.aifeedback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for learning recommendations.
 * Contains suggestions for the next topic a student should study
 * along with an explanation of why.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationDTO {

    private String nextTopic;

    private String explanation;
}
