package com.example.its.aifeedback.controller;

import com.example.its.aifeedback.dto.AIFeedbackDTO;
import com.example.its.aifeedback.dto.AIFeedbackRequestDTO;
import com.example.its.aifeedback.dto.RecommendationDTO;
import com.example.its.aifeedback.dto.ResponseObject;
import com.example.its.aifeedback.service.AIFeedbackQueryService;
import com.example.its.aifeedback.service.AIFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST CONTROLLER - AI Feedback API
 * 
 * Handles HTTP requests for the AI Feedback module.
 * Follows SOLID principles with separated command/query services.
 */
@RestController
@RequestMapping("/api/ai-feedback")
@Tag(name = "AI Feedback", description = "API endpoints for AI-powered feedback generation and retrieval")
public class AIFeedbackController {

    private final AIFeedbackService aiFeedbackService;
    private final AIFeedbackQueryService queryService;

    public AIFeedbackController(AIFeedbackService aiFeedbackService,
            AIFeedbackQueryService queryService) {
        this.aiFeedbackService = aiFeedbackService;
        this.queryService = queryService;
    }

    @Operation(summary = "Generate AI Feedback", description = "Generates intelligent feedback for a student's submission using AI engine. "
            +
            "The feedback includes personalized hints and suggestions based on the answer correctness.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Feedback generated successfully", content = @Content(schema = @Schema(implementation = ResponseObject.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema(implementation = ResponseObject.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Student submission details", required = true, content = @Content(schema = @Schema(implementation = AIFeedbackRequestDTO.class), examples = @ExampleObject(name = "Sample Request", value = """
            {
                "studentId": 1,
                "questionId": 101,
                "questionText": "What is 2 + 2?",
                "studentAnswer": "4",
                "correctAnswer": "4",
                "topic": "Basic Math",
                "difficulty": "easy",
                "subject": "Mathematics"
            }
            """)))
    @PostMapping("/generate")
    public ResponseEntity<ResponseObject<AIFeedbackDTO>> generateFeedback(
            @Valid @RequestBody AIFeedbackRequestDTO request) {

        AIFeedbackDTO feedback = aiFeedbackService.generateFeedback(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseObject.success("Feedback generated successfully", feedback));
    }

    @Operation(summary = "Get Feedback History", description = "Retrieves all feedback records for a specific student. "
            +
            "Returns an empty list if no feedback exists.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feedback history retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/history/{studentId}")
    public ResponseEntity<ResponseObject<List<AIFeedbackDTO>>> getHistory(
            @Parameter(description = "The unique identifier of the student", example = "1") @PathVariable Long studentId) {

        List<AIFeedbackDTO> history = queryService.getFeedbackHistory(studentId);

        return ResponseEntity.ok(
                ResponseObject.success("Feedback history retrieved successfully", history));
    }

    @Operation(summary = "Get Latest Feedback", description = "Retrieves the most recent feedback for a specific student. "
            +
            "Returns 404 if no feedback exists for the student.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Latest feedback retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No feedback found for student"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/latest/{studentId}")
    public ResponseEntity<ResponseObject<AIFeedbackDTO>> getLatestFeedback(
            @Parameter(description = "The unique identifier of the student", example = "1") @PathVariable Long studentId) {

        AIFeedbackDTO feedback = queryService.getLatestFeedback(studentId);

        return ResponseEntity.ok(
                ResponseObject.success("Latest feedback retrieved successfully", feedback));
    }

    @Operation(summary = "Get Learning Recommendations", description = "Retrieves AI-generated learning recommendations for a student. "
            +
            "Suggests next topics and study strategies based on performance.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recommendations retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/recommendations/{studentId}")
    public ResponseEntity<ResponseObject<List<RecommendationDTO>>> getRecommendations(
            @Parameter(description = "The unique identifier of the student", example = "1") @PathVariable Long studentId) {

        List<RecommendationDTO> recommendations = aiFeedbackService.getRecommendations(studentId);

        return ResponseEntity.ok(
                ResponseObject.success("Recommendations retrieved successfully", recommendations));
    }
}
