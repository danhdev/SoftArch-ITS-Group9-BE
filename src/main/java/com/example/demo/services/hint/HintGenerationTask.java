package com.example.demo.services.hint;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.demo.dto.*;
import com.example.demo.models.AIHint;
import com.example.demo.proxy.TestProxyClient;
import com.example.demo.repository.AIHintRepository;
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
    private final TestProxyClient testProxyClient;
    private final HintGenerationBuildPrompt buildPrompt;
    private final AIHintRepository hintRepository;

    @Override
    public AIResponse execute(AIHintRequest request) {
        log.info("Executing hint generation task for student: {}, course: {}, assessment: {}, question: {}",
                request.getStudentId(), request.getCourseId(), request.getAssessmentId(), request.getQuestionId());

        // Fetch test context from external API
        TestResponseDTO testContext = fetchTestContext(request.getCourseId(), request.getAssessmentId());
        
        // Find the specific question
        QuestionDTO targetQuestion = findQuestion(testContext, request.getQuestionId());

        // Fetch subject (course name)
        String subject = fetchCourseName(request.getCourseId());

        // Fetch previous hints from database
        List<String> previousHints = fetchPreviousHints(request.getStudentId(), request.getQuestionId());

        // Fetch course materials
        List<MaterialDTO> materials = fetchCourseMaterials(request.getCourseId());

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

        // Save the generated hint to database
        saveHintToDatabase(request.getStudentId(), request.getQuestionId(), result);

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

    private TestResponseDTO fetchTestContext(String courseId, String assessmentId) {
        try {
            return testProxyClient.getTestByCourseAndAssessment(courseId, assessmentId);
        } catch (Exception e) {
            log.warn("Failed to fetch test context for course: {}, assessment: {}. Error: {}",
                    courseId, assessmentId, e.getMessage());
            return null;
        }
    }

    private QuestionDTO findQuestion(TestResponseDTO testContext, Long questionId) {
        if (testContext == null || testContext.getQuestionList() == null) {
            return null;
        }
        return testContext.getQuestionList().stream()
                .filter(q -> q.getQuestionId() != null && q.getQuestionId().equals(questionId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Fetch course name to use as subject.
     * @param courseId the course identifier
     * @return course name or "Not specified" if not found
     */
    private String fetchCourseName(String courseId) {
        try {
            ResponseObject<List<CourseDTO>> response = testProxyClient.getCourse(courseId);
            if (response != null && response.getData() != null && !response.getData().isEmpty()) {
                String courseName = response.getData().get(0).getName();
                log.info("Fetched course name: {}", courseName);
                return courseName;
            }
        } catch (Exception e) {
            log.warn("Failed to fetch course name for course: {}. Error: {}", courseId, e.getMessage());
        }
        return "Not specified";
    }

    /**
     * Fetch previous hints from database for the student and question.
     * @param studentId the student identifier
     * @param questionId the question identifier
     * @return list of previous hint texts
     */
    private List<String> fetchPreviousHints(Long studentId, Long questionId) {
        try {
            if (studentId == null || questionId == null) {
                return new ArrayList<>();
            }
            List<AIHint> hints = hintRepository.findByStudentIdAndQuestionIdOrderByCreatedAtAsc(studentId, questionId);
            List<String> hintTexts = hints.stream()
                    .map(AIHint::getHint)
                    .collect(Collectors.toList());
            log.info("Fetched {} previous hints for student: {}, question: {}", hintTexts.size(), studentId, questionId);
            return hintTexts;
        } catch (Exception e) {
            log.warn("Failed to fetch previous hints for student: {}, question: {}. Error: {}",
                    studentId, questionId, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Fetch course materials by getting all chapters and their content.
     * @param courseId the course identifier
     * @return list of materials from all chapters
     */
    private List<MaterialDTO> fetchCourseMaterials(String courseId) {
        List<MaterialDTO> allMaterials = new ArrayList<>();
        try {
            // Fetch all chapters for the course
            ResponseObject<List<ChapterDTO>> chaptersResponse = testProxyClient.getCourseChapters(courseId);

            if (chaptersResponse != null && chaptersResponse.getData() != null) {
                List<ChapterDTO> chapters = chaptersResponse.getData();
                log.info("Fetched {} chapters for course: {}", chapters.size(), courseId);

                // Fetch content for each chapter
                for (ChapterDTO chapter : chapters) {
                    try {
                        String chapterId = String.valueOf(chapter.getOrderIndex()); // Using orderIndex as chapterId
                        ResponseObject<ChapterContentResponseDTO> contentResponse =
                                testProxyClient.getChapterContent(courseId, chapterId);

                        if (contentResponse != null && contentResponse.getData() != null
                                && contentResponse.getData().getData() != null) {
                            List<MaterialDTO> chapterMaterials = contentResponse.getData().getData();
                            allMaterials.addAll(chapterMaterials);
                            log.info("Fetched {} materials from chapter: {}", chapterMaterials.size(), chapter.getTitle());
                        }
                    } catch (Exception e) {
                        log.warn("Failed to fetch content for chapter: {}. Error: {}",
                                chapter.getTitle(), e.getMessage());
                    }
                }
            }
            log.info("Total materials fetched: {} for course: {}", allMaterials.size(), courseId);
        } catch (Exception e) {
            log.warn("Failed to fetch course materials for course: {}. Error: {}", courseId, e.getMessage());
        }
        return allMaterials;
    }

    /**
     * Save the generated hint to the database.
     * @param studentId the student identifier
     * @param questionId the question identifier
     * @param hintText the generated hint text
     */
    private void saveHintToDatabase(Long studentId, Long questionId, String hintText) {
        try {
            if (studentId == null || questionId == null || hintText == null) {
                log.warn("Cannot save hint: missing required fields (studentId: {}, questionId: {}, hintText: {})",
                        studentId, questionId, hintText != null ? "present" : "null");
                return;
            }

            AIHint hint = AIHint.builder()
                    .studentId(studentId)
                    .questionId(questionId)
                    .hint(hintText)
                    .createdAt(LocalDateTime.now())
                    .build();

            AIHint savedHint = hintRepository.save(hint);
            log.info("Saved hint to database with ID: {} for student: {}, question: {}",
                    savedHint.getId(), studentId, questionId);
        } catch (Exception e) {
            log.error("Failed to save hint to database for student: {}, question: {}. Error: {}",
                    studentId, questionId, e.getMessage(), e);
        }
    }
}
