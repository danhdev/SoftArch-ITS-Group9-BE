package com.example.demo.services.hint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.demo.dto.*;
import com.example.demo.services.prompt.impl.HintGenerationBuildPrompt;
import com.example.demo.services.prompt.context.HintPromptContext;
import com.example.demo.services.task.AITask;
import org.springframework.stereotype.Component;

import com.example.demo.dto.request.AIHintRequest;
import com.example.demo.llm.LLMClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AI Task for generating hints.
 * Follows Single Responsibility Principle - only handles hint generation logic.
 * Uses BuildPrompt for prompt construction via dependency injection.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class HintGenerationTask implements AITask<AIHintRequest> {

    public static final String TASK_TYPE = "HINT_GENERATION";

    private final LLMClient llmClient;
    private final HintGenerationBuildPrompt buildPrompt;

    @Override
    public AIResponse execute(AIHintRequest request) {
        return execute(request, new ArrayList<>(), null, null, "Not specified", new ArrayList<>());
    }

    /**
     * Execute hint generation with all required context.
     * @param request the hint request
     * @param previousHints list of previous hints for context
     * @param testContext the test context from external API
     * @param targetQuestion the specific question to generate hint for
     * @param subject the course/subject name
     * @param materials list of course materials
     * @return AI response with generated hint
     */
    public AIResponse execute(AIHintRequest request,
                             List<String> previousHints,
                             TestResponseDTO testContext,
                             QuestionDTO targetQuestion,
                             String subject,
                             List<MaterialDTO> materials) {
        log.info("Executing hint generation task for student: {}, course: {}, assessment: {}, question: {}",
                request.getStudentId(), request.getCourseId(), request.getAssessmentId(), request.getQuestionId());

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
