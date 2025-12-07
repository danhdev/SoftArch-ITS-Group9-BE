package com.example.demo.services.prompt.context;

import com.example.demo.dto.TestResponseDTO;
import com.example.demo.dto.request.AIFeedbackRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Context data for building feedback prompts.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackPromptContext {
    private AIFeedbackRequest request;
    private TestResponseDTO testContext;
}
