package com.example.demo.services.material;

import java.util.List;
import java.util.Map;

import com.example.demo.dto.material.ChapterDTO;
import com.example.demo.dto.material.MaterialDTO;
import com.example.demo.services.prompt.impl.MaterialRecommendationBuildPrompt;
import com.example.demo.services.prompt.context.MaterialRecommendationPromptContext;
import com.example.demo.services.task.AITask;
import org.springframework.stereotype.Component;

import com.example.demo.dto.request.AIMaterialRequest;
import com.example.demo.dto.AIResponse;
import com.example.demo.llm.LLMClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AI Task for generating material recommendations.
 * Follows Single Responsibility Principle - only handles material recommendation logic.
 * Uses BuildPrompt for prompt construction via dependency injection.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MaterialRecommendationTask implements AITask<AIMaterialRequest> {

    public static final String TASK_TYPE = "MATERIAL_RECOMMENDATION";

    private final LLMClient llmClient;
    private final MaterialRecommendationBuildPrompt buildPrompt;

    @Override
    public AIResponse execute(AIMaterialRequest request) {
        log.info("Executing material recommendation task for student: {}", request.getStudentId());

        MaterialRecommendationPromptContext context = MaterialRecommendationPromptContext.builder()
                .request(request)
                .build();

        String prompt = buildPrompt.buildPrompt(context);
        log.debug("Generated prompt type: {}", buildPrompt.getPromptType());
        String result = llmClient.chat(prompt);

        return AIResponse.builder()
                .result(result)
                .metadata(Map.of(
                        "taskType", TASK_TYPE,
                        "promptType", buildPrompt.getPromptType().getValue(),
                        "studentId", request.getStudentId() != null ? request.getStudentId() : "unknown"
                ))
                .build();
    }

    /**
     * Execute material recommendation with course materials context.
     *
     * @param request   the AI request containing student preferences
     * @param chapters  list of chapters in the course
     * @param materials list of materials in the course
     * @return AI response with recommendations
     */
    public AIResponse execute(AIMaterialRequest request, List<ChapterDTO> chapters, List<MaterialDTO> materials) {
        log.info("Executing material recommendation task for student: {}, course: {}, with {} chapters and {} materials",
                request.getStudentId(), request.getCourseId(),
                chapters != null ? chapters.size() : 0,
                materials != null ? materials.size() : 0);

        MaterialRecommendationPromptContext context = MaterialRecommendationPromptContext.builder()
                .request(request)
                .chapters(chapters)
                .materials(materials)
                .build();

        String prompt = buildPrompt.buildPrompt(context);
        log.debug("Generated prompt type: {}", buildPrompt.getPromptType());
        String result = llmClient.chat(prompt);

        return AIResponse.builder()
                .result(result)
                .metadata(Map.of(
                        "taskType", TASK_TYPE,
                        "promptType", buildPrompt.getPromptType().getValue(),
                        "studentId", request.getStudentId() != null ? request.getStudentId() : "unknown",
                        "courseId", request.getCourseId() != null ? request.getCourseId() : "unknown",
                        "chaptersCount", chapters != null ? chapters.size() : 0,
                        "materialsCount", materials != null ? materials.size() : 0
                ))
                .build();
    }

    @Override
    public String getTaskType() {
        return TASK_TYPE;
    }
}
