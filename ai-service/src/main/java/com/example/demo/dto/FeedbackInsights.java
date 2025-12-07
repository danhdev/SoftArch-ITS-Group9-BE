package com.example.demo.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for extracted feedback insights.
 * Contains skills improved, weak points, and suggested resources.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackInsights {

    private List<String> skillsImproved;

    private List<String> weakPoints;

    private List<String> suggestedResources;
}
