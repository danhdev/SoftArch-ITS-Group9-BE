package com.example.demo.services.material;

import java.util.List;
import java.util.Map;

import com.example.demo.dto.AIResponse;
import com.example.demo.dto.MaterialContentResponseDTO;
import com.example.demo.dto.request.AIExplainRequest;
import com.example.demo.llm.LLMClient;
import com.example.demo.models.AIExplanation;
import com.example.demo.services.dataprovider.CourseDataProvider;
import com.example.demo.services.dataprovider.ExplanationDataProvider;
import com.example.demo.services.prompt.context.MaterialExplanationPromptContext;
import com.example.demo.services.prompt.impl.MaterialExplanationBuildPrompt;
import com.example.demo.services.task.AITask;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AI Task for generating material explanations.
 * Follows Single Responsibility Principle - handles material explanation logic and data gathering.
 * Follows Dependency Inversion Principle - depends on abstractions (interfaces).
 * Uses BuildPrompt for prompt construction via dependency injection.
 * Uses DataProviders to gather necessary data for material explanations.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MaterialExplanationTask implements AITask<AIExplainRequest> {

    public static final String TASK_TYPE = "MATERIAL_EXPLANATION";

    private final LLMClient llmClient;
    private final MaterialExplanationBuildPrompt buildPrompt;

    // DataProviders for SOLID compliance - separate data access concerns
    private final ExplanationDataProvider explanationDataProvider;
    private final CourseDataProvider courseDataProvider;

    @Override
    public AIResponse execute(AIExplainRequest request) {
        log.info("Executing material explanation task for student: {}, material: {}",
                request.getStudentId(), request.getMaterialId());

        // Delegate data fetching to ExplanationDataProvider (SRP compliance)
        List<AIExplanation> previousExplanations = explanationDataProvider.getPreviousExplanations(
                request.getStudentId(),
                request.getMaterialId()
        );

        List<String> previousQuestions = explanationDataProvider.extractQuestions(previousExplanations);
        List<String> previousAnswers = explanationDataProvider.extractAnswers(previousExplanations);

        log.info("Using {} previous explanations for context", previousQuestions.size());

        // Delegate material content fetching to CourseDataProvider (SRP compliance)
        MaterialContentResponseDTO materialContent = courseDataProvider.getMaterialContent(request.getMaterialId());

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

        AIResponse response = AIResponse.builder()
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

        // Delegate saving to ExplanationDataProvider (SRP compliance)
        if (result != null) {
            explanationDataProvider.saveExplanation(
                    request.getStudentId(),
                    request.getMaterialId(),
                    request.getStudentQuestion(),
                    result
            );
        }

        return response;
    }

    @Override
    public String getTaskType() {
        return TASK_TYPE;
    }
}
