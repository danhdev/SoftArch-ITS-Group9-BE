package com.example.demo.services.hint;

import org.springframework.stereotype.Service;

import com.example.demo.dto.*;
import com.example.demo.dto.request.AIHintRequest;
import com.example.demo.proxy.MaterialProxyClient;
import com.example.demo.proxy.TestProxyClient;
import com.example.demo.models.AIHint;
import com.example.demo.repository.AIHintRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of HintService.
 * Follows Single Responsibility Principle - handles only hint generation operations.
 * Uses Strategy Pattern via AI Tasks.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HintServiceImpl implements HintService {

    private final HintGenerationTask hintGenerationTask;
    private final AIHintRepository hintRepository;
    private final TestProxyClient testProxyClient;
    private final MaterialProxyClient materialProxyClient;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public AIResponse hint(AIHintRequest request) {
        log.info("Processing hint generation request for student: {}, course: {}, assessment: {}, question: {}",
                request.getStudentId(), request.getCourseId(), request.getAssessmentId(), request.getQuestionId());

        // Fetch previous hints from database before generating new hint
        List<String> previousHints = fetchPreviousHints(request.getStudentId(), request.getQuestionId());

        // Fetch test context from external API
        TestResponseDTO testContext = fetchTestContext(request.getCourseId(), request.getAssessmentId());

        // Find the specific question
        QuestionDTO targetQuestion = findQuestion(testContext, request.getQuestionId());

        // Fetch subject (course name)
        String subject = fetchCourseName(request.getCourseId());

        // Fetch course materials
        List<MaterialDTO> materials = fetchCourseMaterials(request.getCourseId());

        // Execute the hint generation task with all context
        AIResponse response = hintGenerationTask.execute(
                request,
                previousHints,
                testContext,
                targetQuestion,
                subject,
                materials
        );

        // Save the generated hint to database
        if (response != null && response.getResult() != null) {
            saveHintToDatabase(request.getStudentId(), request.getQuestionId(), response.getResult());
        }

        return response;
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
                    .toList();
            log.info("Fetched {} previous hints for student: {}, question: {}", hintTexts.size(), studentId, questionId);
            return hintTexts;
        } catch (Exception e) {
            log.warn("Failed to fetch previous hints for student: {}, question: {}. Error: {}",
                    studentId, questionId, e.getMessage());
            return new ArrayList<>();
        }
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
                    .build();

            AIHint savedHint = hintRepository.save(hint);
            log.info("Saved hint to database with ID: {} for student: {}, question: {}",
                    savedHint.getId(), studentId, questionId);
        } catch (Exception e) {
            log.error("Failed to save hint to database for student: {}, question: {}. Error: {}",
                    studentId, questionId, e.getMessage(), e);
        }
    }

    /**
     * Fetch test context from external API.
     * @param courseId the course identifier
     * @param assessmentId the assessment identifier
     * @return test context or null if fetch fails
     */
    private TestResponseDTO fetchTestContext(String courseId, String assessmentId) {
        try {
            TestResponseDTO testContext = testProxyClient.getTestByCourseAndAssessment(courseId, assessmentId);
            log.info("Fetched test context for course: {}, assessment: {}", courseId, assessmentId);
            return testContext;
        } catch (Exception e) {
            log.warn("Failed to fetch test context for course: {}, assessment: {}. Error: {}",
                    courseId, assessmentId, e.getMessage());
            return null;
        }
    }

    /**
     * Find a specific question in the test context.
     * @param testContext the test response containing questions
     * @param questionId the question identifier
     * @return the question or null if not found
     */
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
                                materialProxyClient.getChapterContent(courseId, chapterId);

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

    @Override
    public List<HintResponseDTO> getHintHistory(Long studentId, Long questionId) {
        log.info("Fetching hint history for student: {}, question: {}", studentId, questionId);

        List<AIHint> hints = hintRepository.findByStudentIdAndQuestionIdOrderByCreatedAtAsc(studentId, questionId);
        List<HintResponseDTO> hintDTOs = new ArrayList<>();

        for (int i = 0; i < hints.size(); i++) {
            AIHint hint = hints.get(i);
            HintResponseDTO dto = HintResponseDTO.builder()
                    .hintId(hint.getId())
                    .questionId(hint.getQuestionId())
                    .studentId(hint.getStudentId())
                    .hint(hint.getHint())
                    .hintCount(i + 1)
                    .createdAt(hint.getCreatedAt() != null ? hint.getCreatedAt().format(DATE_FORMATTER) : null)
                    .build();
            hintDTOs.add(dto);
        }

        log.info("Found {} hints for student: {}, question: {}", hintDTOs.size(), studentId, questionId);

        return hintDTOs;
    }
}
