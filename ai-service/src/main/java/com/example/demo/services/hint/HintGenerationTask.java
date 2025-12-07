package com.example.demo.services.hint;

import java.util.List;
import java.util.Map;

import com.example.demo.dto.*;
import com.example.demo.dto.request.AIHintRequest;
import com.example.demo.llm.LLMClient;
import com.example.demo.services.dataprovider.CourseDataProvider;
import com.example.demo.services.dataprovider.HintDataProvider;
import com.example.demo.services.dataprovider.TestDataProvider;
import com.example.demo.services.prompt.context.HintPromptContext;
import com.example.demo.services.prompt.impl.HintGenerationBuildPrompt;
import com.example.demo.services.task.AITask;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AI Task for generating hints.
 * Follows Single Responsibility Principle - handles hint generation logic and data gathering.
 * Follows Dependency Inversion Principle - depends on abstractions (interfaces).
 * Uses BuildPrompt for prompt construction via dependency injection.
 * Uses DataProviders to gather necessary data for hint generation.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class HintGenerationTask implements AITask<AIHintRequest> {

    public static final String TASK_TYPE = "HINT_GENERATION";

    private final LLMClient llmClient;
    private final HintGenerationBuildPrompt buildPrompt;

    // DataProviders for SOLID compliance - separate data access concerns
    private final HintDataProvider hintDataProvider;
    private final TestDataProvider testDataProvider;
    private final CourseDataProvider courseDataProvider;

    @Override
    public AIResponse execute(AIHintRequest request) {
        log.info("Executing hint generation task for student: {}, course: {}, assessment: {}, question: {}",
                request.getStudentId(), request.getCourseId(), request.getAssessmentId(), request.getQuestionId());

        // Delegate data fetching to DataProviders (SRP compliance)
        List<String> previousHints = hintDataProvider.getPreviousHintTexts(request.getStudentId(), request.getQuestionId());

        // Fetch test context from TestDataProvider
        TestResponseDTO testContext = testDataProvider.getTestContext(request.getCourseId(), request.getAssessmentId());

        // Find the specific question (returns Optional, use orElse for null fallback)
        QuestionDTO targetQuestion = testDataProvider.findQuestion(testContext, request.getQuestionId()).orElse(null);

        // Fetch subject (course name) from CourseDataProvider
        String subject = courseDataProvider.getCourseName(request.getCourseId());

        // Fetch course materials from CourseDataProvider
        List<MaterialDTO> materials = courseDataProvider.getCourseMaterials(request.getCourseId());

        // Build context with all fields
        HintPromptContext context = HintPromptContext.builder()
                .request(request)
                .testContext(testContext)
                .targetQuestion(targetQuestion)
                .subject(subject)
                .previousHints(previousHints)
                .materials(materials)
                .build();

        String prompt = buildPrompt.buildPrompt(context);
        log.debug("Generated prompt type: {}", buildPrompt.getPromptType());

        // Generate hint using LLM
        String result = llmClient.chat(prompt);

        // Save hint to database (SRP compliance)
        if (result != null) {
            hintDataProvider.saveHint(request.getStudentId(), request.getQuestionId(), result);
        }

        return AIResponse.builder()
                .result(result)
                .metadata(Map.of(
                        "taskType", TASK_TYPE,
                        "promptType", buildPrompt.getPromptType().getValue(),
                        "studentId", request.getStudentId() != null ? request.getStudentId().toString() : "unknown",
                        "courseId", request.getCourseId() != null ? request.getCourseId() : "unknown",
                        "assessmentId", request.getAssessmentId() != null ? request.getAssessmentId() : "unknown",
                        "questionId", request.getQuestionId() != null ? request.getQuestionId().toString() : "unknown",
                        "previousHintsCount", previousHints.size(),
                        "materialsCount", materials.size()
                ))
                .build();
    }

    @Override
    public String getTaskType() {
        return TASK_TYPE;
    }
}
