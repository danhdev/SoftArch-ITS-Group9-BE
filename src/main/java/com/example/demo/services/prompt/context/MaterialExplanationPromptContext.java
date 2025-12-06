package com.example.demo.services.prompt.context;

import com.example.demo.dto.request.AIFeedbackRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Context data for building material explanation prompts.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialExplanationPromptContext {
    private String studentQuestion;

    private String materialContent;

    private String fileName;

    private String pages;

    private List<String> previousQuestions;

    private List<String> previousExplanations;
}
