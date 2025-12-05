package com.example.its.aifeedback.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HintSubmissionContext {

    private Long studentId;

    private Long questionId;

    private String questionText;

    private String correctAnswer;

    private String subject;

    private String topic;

    private String difficulty;

    private List<String> previousHints;
}

