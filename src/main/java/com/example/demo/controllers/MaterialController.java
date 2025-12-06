package com.example.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.AIFeedbackRequest;
import com.example.demo.dto.AIResponse;
import com.example.demo.dto.ResponseObject;
import com.example.demo.services.material.IMaterialService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for material-related operations.
 * Follows Single Responsibility Principle - handles only HTTP concerns for material endpoints.
 * Delegates business logic to MaterialService.
 */
@RestController
@RequestMapping("/api/v1/materials")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Material", description = "Material recommendation and explanation endpoints")
public class MaterialController {

    private final IMaterialService materialService;

    /**
     * Recommend learning materials based on student context.
     *
     * @param request the AI request containing student context
     * @return response with material recommendations
     */
    @PostMapping("/recommend")
    @Operation(summary = "Recommend learning materials",
               description = "Generates personalized learning material recommendations based on student context and progress")
    public ResponseEntity<ResponseObject<AIResponse>> recommend(@Valid @RequestBody AIFeedbackRequest request) {
        log.info("Received material recommendation request for student: {}", request.getStudentId());
        
        AIResponse response = materialService.recommend(request);
        
        return ResponseEntity.ok(ResponseObject.success("Material recommendations generated successfully", response));
    }

    /**
     * Explain a topic or concept.
     *
     * @param request the AI request containing the topic to explain
     * @return response with explanation
     */
    @PostMapping("/explain")
    @Operation(summary = "Explain a topic",
               description = "Provides a detailed explanation of a topic or concept suitable for student learning")
    public ResponseEntity<ResponseObject<AIResponse>> explain(@Valid @RequestBody AIFeedbackRequest request) {
        log.info("Received material explanation request");
        
        AIResponse response = materialService.explain(request);
        
        return ResponseEntity.ok(ResponseObject.success("Explanation generated successfully", response));
    }
}
