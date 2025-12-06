package com.example.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.AIHintRequest;
import com.example.demo.dto.AIResponse;
import com.example.demo.dto.ResponseObject;
import com.example.demo.services.hint.HintService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
        log.info("Received hint generation request for course: {}, assessment: {}, question: {}",
                request.getCourseId(), request.getAssessmentId(), request.getQuestionId());
        
        AIResponse response = hintService.hint(request);
        
        return ResponseEntity.ok(ResponseObject.success("Hint generated successfully", response));
    }
}
