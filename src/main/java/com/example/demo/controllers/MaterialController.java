package com.example.demo.controllers;

import com.example.demo.dto.AIExplainResponseDTO;
import com.example.demo.dto.request.AIExplainRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.request.AIMaterialRequest;
import com.example.demo.dto.request.AIFeedbackRequest;
import com.example.demo.dto.AIResponse;
import com.example.demo.dto.ResponseObject;
import com.example.demo.services.material.IMaterialService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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
     * Recommend learning materials based on student context and course materials.
     *
     * @param request the AI request containing student context and course ID
     * @return response with material recommendations
     */
    @PostMapping("/recommend")
    @Operation(summary = "Recommend learning materials",
               description = "Generates personalized learning material recommendations based on student context, course materials, and student preferences")
    public ResponseEntity<ResponseObject<AIResponse>> recommend(@Valid @RequestBody AIMaterialRequest request) {
        log.info("Received material recommendation request for student: {}, course: {}", 
                request.getStudentId(), request.getCourseId());
        
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
    public ResponseEntity<ResponseObject<AIResponse>> explain(@Valid @RequestBody AIExplainRequest request) {
        log.info("Received material explanation request");
        
        AIResponse response = materialService.explain(request);
        
        return ResponseEntity.ok(ResponseObject.success("Explanation generated successfully", response));
    }

    /**
     * Get explanation history for a specific student and material.
     *
     * @param studentId  the student identifier
     * @param materialId the material identifier
     * @return list of explanations ordered by creation time
     */
    @GetMapping("/explain/history")
    @Operation(summary = "Get explanation history",
               description = "Retrieves all explanations generated for a specific student and material, ordered by creation time")
    public ResponseEntity<ResponseObject<List<AIExplainResponseDTO>>> getExplainHistory(
            @Parameter(description = "ID of the student", example = "1", required = true)
            @RequestParam Long studentId,
            @Parameter(description = "ID of the material", example = "5", required = true)
            @RequestParam Long materialId) {

        log.info("Retrieving explanation history for student: {}, material: {}", studentId, materialId);

        List<AIExplainResponseDTO> history = materialService.getExplainHistory(studentId, materialId);

        return ResponseEntity.ok(ResponseObject.success("Explanation history retrieved successfully", history));
    }
}
