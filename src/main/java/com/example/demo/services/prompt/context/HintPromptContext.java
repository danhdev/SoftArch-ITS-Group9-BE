package com.example.demo.services.prompt.context;

import com.example.demo.dto.QuestionDTO;
import com.example.demo.dto.TestResponseDTO;
import com.example.demo.dto.request.AIHintRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Context data for building hint prompts.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HintPromptContext {
    private AIHintRequest request;
    private TestResponseDTO testContext;
    private QuestionDTO targetQuestion;
}
