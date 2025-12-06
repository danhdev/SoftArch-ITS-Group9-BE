package com.example.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.request.AIHintRequest;
import com.example.demo.dto.AIResponse;
import com.example.demo.dto.HintResponseDTO;
import com.example.demo.dto.ResponseObject;
import com.example.demo.services.hint.HintService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * REST Controller for hint-related operations.
 * Follows Single Responsibility Principle - handles only HTTP concerns for hint endpoints.
 * Delegates business logic to HintService.
 */
@RestController
@RequestMapping("/api/v1/hints")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Hint", description = "Hint generation endpoints")
public class HintController {

    private final HintService hintService;

    /**
     * Generate a hint for a problem/question.
     *
     * @param request the AI hint request containing course, assessment, and question identifiers
     * @return response with the generated hint
     */
    @PostMapping("/generate")
    @Operation(summary = "Generate a hint",
               description = "Generates a helpful hint for a specific question without revealing the complete answer")
    public ResponseEntity<ResponseObject<AIResponse>> generateHint(@Valid @RequestBody AIHintRequest request) {
        log.info("Received hint generation request for student: {}, course: {}, assessment: {}, question: {}",
                request.getStudentId(), request.getCourseId(), request.getAssessmentId(), request.getQuestionId());

        AIResponse response = hintService.hint(request);
        
        return ResponseEntity.ok(ResponseObject.success("Hint generated successfully", response));
    }

    /**
     * Get hint history for a specific student and question.
     *
     * @param studentId the student identifier
     * @param questionId the question identifier
     * @return list of hints ordered by creation time
     */
    @GetMapping("/history")
    @Operation(summary = "Get hint history",
               description = "Retrieves all hints generated for a specific student and question, ordered by creation time")
    public ResponseEntity<ResponseObject<List<HintResponseDTO>>> getHintHistory(
            @Parameter(description = "ID of the student", example = "1", required = true)
            @RequestParam Long studentId,
            @Parameter(description = "ID of the question", example = "101", required = true)
            @RequestParam Long questionId) {

        log.info("Retrieving hint history for student: {}, question: {}", studentId, questionId);

        List<HintResponseDTO> history = hintService.getHintHistory(studentId, questionId);

        return ResponseEntity.ok(ResponseObject.success("Hint history retrieved successfully", history));
    }
}
