package com.example.demo.services.material;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.demo.dto.MaterialContentResponseDTO;
import com.example.demo.dto.request.AIExplainRequest;
import com.example.demo.models.AIExplanation;
import com.example.demo.proxy.TestProxyClient;
import com.example.demo.repository.AIExplanationRepository;
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
    private final AIExplanationRepository explanationRepository;
    private final TestProxyClient testProxyClient;

    @Override
    public AIResponse execute(AIExplainRequest request) {
        log.info("Executing material explanation task for student: {}, material: {}",
                request.getStudentId(), request.getMaterialId());

        // Fetch previous explanations from database
        List<AIExplanation> previousExplanations = getPreviousExplanations(
                request.getStudentId(),
                request.getMaterialId()
        );

        List<String> previousQuestions = previousExplanations.stream()
                .map(AIExplanation::getStudentQuestion)
                .collect(Collectors.toList());

        List<String> previousAnswers = previousExplanations.stream()
                .map(AIExplanation::getExplanation)
                .collect(Collectors.toList());

        log.info("Found {} previous explanations for student: {}, material: {}",
                previousExplanations.size(), request.getStudentId(), request.getMaterialId());

        // Fetch material content from external API
        MaterialContentResponseDTO materialContent = fetchMaterialContent(
                request.getMaterialId()
        );

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

        // Save explanation to database
        saveExplanationToDatabase(
                request.getStudentId(),
                request.getMaterialId(),
                request.getStudentQuestion(),
                result
        );

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

    /**
     * Fetch previous explanations from database for the student and material.
     * @param studentId the student identifier
     * @param materialId the material identifier
     * @return list of previous explanations
     */
    private List<AIExplanation> getPreviousExplanations(Long studentId, Long materialId) {
        try {
            if (studentId == null || materialId == null) {
                return new ArrayList<>();
            }
            List<AIExplanation> explanations = explanationRepository
                    .findByStudentIdAndMaterialIdOrderByCreatedAtAsc(studentId, materialId);
            log.info("Fetched {} previous explanations for student: {}, material: {}",
                    explanations.size(), studentId, materialId);
            return explanations;
        } catch (Exception e) {
            log.warn("Failed to fetch previous explanations for student: {}, material: {}. Error: {}",
                    studentId, materialId, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Fetch material content from external API.
     * @param materialId the material identifier
     * @return material content response or null if fetch fails
     */
    private MaterialContentResponseDTO fetchMaterialContent(Long materialId) {
        try {
            var response = testProxyClient.getMaterialContent(materialId);

            if (response != null && response.getData() != null) {
                MaterialContentResponseDTO content = response.getData();
                log.info("Fetched material content: file={}, pages={}",
                        content.getFileName(), content.getPages());
                return content;
            }

            log.warn("No material content found for material: {}", materialId);
            return null;
        } catch (Exception e) {
            log.warn("Failed to fetch material content for material: {}. Error: {}",
                    materialId, e.getMessage());
            return null;
        }
    }

    /**
     * Save the generated explanation to the database.
     * @param studentId the student identifier
     * @param materialId the material identifier
     * @param studentQuestion the student's question
     * @param explanation the generated explanation
     */
    private void saveExplanationToDatabase(Long studentId, Long materialId,
                                          String studentQuestion, String explanation) {
        try {
            if (studentId == null || materialId == null || explanation == null) {
                log.warn("Cannot save explanation: missing required fields (studentId: {}, materialId: {}, explanation: {})",
                        studentId, materialId, explanation != null ? "present" : "null");
                return;
            }

            AIExplanation explanationEntity = AIExplanation.builder()
                    .studentId(studentId)
                    .materialId(materialId)
                    .studentQuestion(studentQuestion)
                    .explanation(explanation)
                    .createdAt(LocalDateTime.now())
                    .build();

            AIExplanation savedExplanation = explanationRepository.save(explanationEntity);
            log.info("Saved explanation to database with ID: {} for student: {}, material: {}",
                    savedExplanation.getId(), studentId, materialId);
        } catch (Exception e) {
            log.error("Failed to save explanation to database for student: {}, material: {}. Error: {}",
                    studentId, materialId, e.getMessage(), e);
        }
    }
}
