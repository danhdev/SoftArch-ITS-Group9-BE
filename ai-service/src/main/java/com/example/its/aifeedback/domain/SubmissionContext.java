package com.example.its.aifeedback.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model representing the context of a student's submission.
 * This is NOT a JPA entity - it's a value object used in the domain layer
 * to encapsulate submission data for AI processing.
 * 
 * Following DDD principles: This is an immutable value object that
 * carries information about a specific submission context.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionContext {

    private Long studentId;

    private Long questionId;

    private String questionText;

    private String studentAnswer;

    private String correctAnswer;

    private String topic;

    private String difficulty;

    private String subject;
}
