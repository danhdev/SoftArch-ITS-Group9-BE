package com.example.demo.services.material;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.demo.dto.MaterialContentResponseDTO;
import com.example.demo.dto.request.AIExplainRequest;
import com.example.demo.services.prompt.impl.MaterialExplanationBuildPrompt;
import com.example.demo.services.prompt.context.MaterialExplanationPromptContext;
import com.example.demo.services.task.AITask;
import org.springframework.stereotype.Component;

import com.example.demo.dto.AIResponse;
import com.example.demo.llm.LLMClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AI Task for generating material explanations.
 * Follows Single Responsibility Principle - only handles material explanation logic.
 * Uses BuildPrompt for prompt construction via dependency injection.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MaterialExplanationTask implements AITask<AIExplainRequest> {

    public static final String TASK_TYPE = "MATERIAL_EXPLANATION";

    private final LLMClient llmClient;
    private final MaterialExplanationBuildPrompt buildPrompt;

    @Override
    public AIResponse execute(AIExplainRequest request) {
        return execute(request, new ArrayList<>(), new ArrayList<>(), null);
    }

    /**
     * Execute explanation generation with previous Q&A context and material content.
     * @param request the explanation request
     * @param previousQuestions list of previous questions for context
     * @param previousAnswers list of previous answers for context
     * @param materialContent the material content from external API
     * @return AI response with generated explanation
     */
    public AIResponse execute(AIExplainRequest request,
                             List<String> previousQuestions,
                             List<String> previousAnswers,
                             MaterialContentResponseDTO materialContent) {
        log.info("Executing material explanation task for student: {}, material: {}",
                request.getStudentId(), request.getMaterialId());

        log.info("Using {} previous explanations for context", previousQuestions.size());


        // Build context with all required data
        MaterialExplanationPromptContext context = MaterialExplanationPromptContext.builder()
                .studentQuestion(request.getStudentQuestion())
                .materialContent(materialContent != null ? materialContent.getContent() : null)
                .fileName(materialContent != null ? materialContent.getFileName() : null)
                .pages(materialContent != null ? materialContent.getPages() : null)
                .previousQuestions(previousQuestions)
                .previousExplanations(previousAnswers)
                .build();

        // Generate prompt and get LLM response
        String prompt = buildPrompt.buildPrompt(context);
        log.debug("Generated prompt type: {}", buildPrompt.getPromptType());

        String result = llmClient.chat(prompt);


        return AIResponse.builder()
                .result(result)
                .metadata(Map.of(
                        "taskType", TASK_TYPE,
                        "promptType", buildPrompt.getPromptType().getValue(),
                        "studentId", request.getStudentId() != null ? request.getStudentId().toString() : "unknown",
                        "materialId", request.getMaterialId() != null ? request.getMaterialId().toString() : "unknown",
                        "previousQuestionsCount", previousQuestions.size(),
                        "materialFileName", materialContent != null ? materialContent.getFileName() : "not available"
                ))
                .build();
    }

    @Override
    public String getTaskType() {
        return TASK_TYPE;
    }
}
