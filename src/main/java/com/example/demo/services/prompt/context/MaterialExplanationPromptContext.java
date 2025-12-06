package com.example.demo.services.prompt.context;

import com.example.demo.dto.request.AIFeedbackRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Context data for building material explanation prompts.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialExplanationPromptContext {
    private AIFeedbackRequest request;
}
