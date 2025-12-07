package com.example.demo.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.AIFeedbackRequest;
import com.example.demo.dto.AIResponse;
import com.example.demo.dto.ResponseObject;
import com.example.demo.models.FeedbackRecord;
import com.example.demo.services.feedback.ITestFeedbackService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for feedback generation operations.
 * Follows Single Responsibility Principle - handles only HTTP concerns for feedback endpoints.
 * Delegates business logic to FeedbackService.
 */
@RestController
@RequestMapping("/api/v1/feedback")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Feedback", description = "Feedback generation and history endpoints")
public class FeedbackController {

    private final ITestFeedbackService feedbackService;

    /**
     * Generate feedback for a student submission.
     *
     * @param request the AI request containing the submission
     * @return response with generated feedback
     */
    @PostMapping("generate")
    @Operation(summary = "Generate feedback",
               description = "Generates comprehensive AI-powered feedback for a student submission")
    public ResponseEntity<ResponseObject<AIResponse>> generateFeedback(
            @Valid @RequestBody AIFeedbackRequest request) {
        log.info("Received feedback generation request for course: {}, assessment: {}, student: {}",
                request.getCourseId(), request.getAssessmentId(), request.getStudentId());
        
        AIResponse response = feedbackService.feedback(request);
        
        return ResponseEntity.ok(ResponseObject.success("Feedback generated successfully", response));
    }

    /**
     * Get feedback history for a student.
     *
     * @param studentId the student's unique identifier
     * @return list of feedback records
     */
    @GetMapping("/history/{studentId}")
    @Operation(summary = "Get feedback history",
               description = "Retrieves all feedback records for a specific student")
    public ResponseEntity<ResponseObject<List<FeedbackRecord>>> history(
            @Parameter(description = "Student's unique identifier")
            @PathVariable String studentId) {
        log.info("Received request for feedback history of student: {}", studentId);
        
        List<FeedbackRecord> history = feedbackService.getHistory(studentId);
        
        return ResponseEntity.ok(ResponseObject.success("Feedback history retrieved successfully", history));
    }

    /**
     * Get feedback by student and assessment.
     *
     * @param studentId    the student's unique identifier
     * @param assessmentId the assessment's unique identifier
     * @return feedback record if found
     */
    @GetMapping("/student/{studentId}/assessment/{assessmentId}")
    @Operation(summary = "Get feedback by assessment",
               description = "Retrieves feedback for a specific student and assessment combination")
    public ResponseEntity<ResponseObject<FeedbackRecord>> byAssessment(
            @Parameter(description = "Student's unique identifier")
            @PathVariable String studentId,
            @Parameter(description = "Assessment's unique identifier")
            @PathVariable String assessmentId) {
        log.info("Received request for feedback of student: {}, assessment: {}", studentId, assessmentId);
        
        return feedbackService.getByAssessment(studentId, assessmentId)
                .map(record -> ResponseEntity.ok(ResponseObject.success("Feedback retrieved successfully", record)))
                .orElse(ResponseEntity.ok(ResponseObject.error(404, "Feedback not found")));
    }
}
