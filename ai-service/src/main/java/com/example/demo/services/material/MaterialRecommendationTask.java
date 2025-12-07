package com.example.demo.services.material;

import java.util.List;
import java.util.Map;

import com.example.demo.dto.material.ChapterDTO;
import com.example.demo.dto.material.MaterialDTO;
import com.example.demo.dto.request.AIMaterialRequest;
import com.example.demo.dto.AIResponse;
import com.example.demo.llm.LLMClient;
import com.example.demo.services.dataprovider.CourseDataProvider;
import com.example.demo.services.dataprovider.RecommendationDataProvider;
import com.example.demo.services.prompt.context.MaterialRecommendationPromptContext;
import com.example.demo.services.prompt.impl.MaterialRecommendationBuildPrompt;
import com.example.demo.services.task.AITask;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AI Task for generating material recommendations.
 * Follows Single Responsibility Principle - handles material recommendation logic and data gathering.
 * Follows Dependency Inversion Principle - depends on abstractions (interfaces).
 * Uses BuildPrompt for prompt construction via dependency injection.
 * Uses DataProviders to gather necessary data for material recommendations.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MaterialRecommendationTask implements AITask<AIMaterialRequest> {

    public static final String TASK_TYPE = "MATERIAL_RECOMMENDATION";

    private final LLMClient llmClient;
    private final MaterialRecommendationBuildPrompt buildPrompt;

    // DataProviders for SOLID compliance - separate data access concerns
    private final CourseDataProvider courseDataProvider;
    private final RecommendationDataProvider recommendationDataProvider;

    @Override
    public AIResponse execute(AIMaterialRequest request) {
        log.info("Executing material recommendation task for student: {}, course: {}",
                request.getStudentId(), request.getCourseId());

        // Delegate data fetching to CourseDataProvider (SRP compliance)
        List<ChapterDTO> chapters = courseDataProvider.getMaterialChapters(request.getCourseId());
        log.info("Fetched {} chapters for course: {}", chapters.size(), request.getCourseId());

        List<MaterialDTO> allMaterials = courseDataProvider.getMaterialsForChapters(chapters);
        log.info("Fetched {} total materials from {} chapters", allMaterials.size(), chapters.size());

        // Build context with all required data
        MaterialRecommendationPromptContext context = MaterialRecommendationPromptContext.builder()
                .request(request)
                .chapters(chapters)
                .materials(allMaterials)
                .build();

        String prompt = buildPrompt.buildPrompt(context);
        log.debug("Generated prompt type: {}", buildPrompt.getPromptType());

        // Generate recommendation using LLM
        String result = llmClient.chat(prompt);

        AIResponse response = AIResponse.builder()
                .result(result)
                .metadata(Map.of(
                        "taskType", TASK_TYPE,
                        "promptType", buildPrompt.getPromptType().getValue(),
                        "studentId", request.getStudentId() != null ? request.getStudentId() : "unknown",
                        "courseId", request.getCourseId() != null ? request.getCourseId() : "unknown",
                        "chaptersCount", chapters.size(),
                        "materialsCount", allMaterials.size()
                ))
                .build();

        // Delegate saving to RecommendationDataProvider (SRP compliance)
        recommendationDataProvider.saveRecommendation(request, response);

        return response;
    }

    @Override
    public String getTaskType() {
        return TASK_TYPE;
    }
}
