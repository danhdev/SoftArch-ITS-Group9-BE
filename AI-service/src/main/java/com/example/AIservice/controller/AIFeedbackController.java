package com.example.AIservice.controller;

import com.example.AIservice.dto.AIFeedbackDTO;
import com.example.AIservice.dto.AIFeedbackRequestDTO;
import com.example.AIservice.dto.ResponseObject;
import com.example.AIservice.dto.recommendationDTO;
import com.example.AIservice.service.AIFeedbackQueryService;
import com.example.AIservice.service.AIFeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AIFeedbackController
 * SRP: Handles HTTP request/response for AI feedback operations
 * DIP: Depends on interfaces (AIFeedbackService, AIFeedbackQueryService), not concrete implementations
 */
@RestController
@RequestMapping("/api/ai-feedback")
public class AIFeedbackController {

    private final AIFeedbackService aiFeedbackService;
    private final AIFeedbackQueryService queryService;

    public AIFeedbackController(AIFeedbackService aiFeedbackService, AIFeedbackQueryService queryService) {
        this.aiFeedbackService = aiFeedbackService;
        this.queryService = queryService;
    }

    /**
     * Generate AI feedback for a student's answer
     */
    @PostMapping("/generate")
    public ResponseObject<AIFeedbackDTO> generateFeedback(@RequestBody AIFeedbackRequestDTO req) {
        try {
            AIFeedbackDTO feedback = aiFeedbackService.generateFeedback(req);
            return ResponseObject.<AIFeedbackDTO>builder()
                    .status(HttpStatus.OK.value())
                    .message("Feedback generated successfully")
                    .data(feedback)
                    .build();
        } catch (Exception e) {
            return ResponseObject.<AIFeedbackDTO>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Error generating feedback: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * Get learning recommendations for a student
     */
    @GetMapping("/recommendations/{studentId}")
    public ResponseObject<List<recommendationDTO>> getRecommendations(@PathVariable Long studentId) {
        try {
            List<recommendationDTO> recommendations = aiFeedbackService.getRecommendations(studentId);
            return ResponseObject.<List<recommendationDTO>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Recommendations retrieved successfully")
                    .data(recommendations)
                    .build();
        } catch (Exception e) {
            return ResponseObject.<List<recommendationDTO>>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Error retrieving recommendations: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * Get feedback history for a student
     */
    @GetMapping("/history/{studentId}")
    public ResponseObject<List<AIFeedbackDTO>> getHistory(@PathVariable Long studentId) {
        try {
            List<AIFeedbackDTO> history = queryService.getFeedbackHistory(studentId);
            return ResponseObject.<List<AIFeedbackDTO>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Feedback history retrieved successfully")
                    .data(history)
                    .build();
        } catch (Exception e) {
            return ResponseObject.<List<AIFeedbackDTO>>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Error retrieving feedback history: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * Get latest feedback for a student
     */
    @GetMapping("/latest/{studentId}")
    public ResponseObject<AIFeedbackDTO> getLatestFeedback(@PathVariable Long studentId) {
        try {
            AIFeedbackDTO feedback = queryService.getLatestFeedback(studentId);
            if (feedback != null) {
                return ResponseObject.<AIFeedbackDTO>builder()
                        .status(HttpStatus.OK.value())
                        .message("Latest feedback retrieved successfully")
                        .data(feedback)
                        .build();
            } else {
                return ResponseObject.<AIFeedbackDTO>builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .message("No feedback found for student")
                        .data(null)
                        .build();
            }
        } catch (Exception e) {
            return ResponseObject.<AIFeedbackDTO>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Error retrieving latest feedback: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }
}

