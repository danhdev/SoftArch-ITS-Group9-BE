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
public class ExplainSubmissionContext {
    private String studentQuestion;

    private String materialContent;

    private String fileName;

    private String pages;

    private List<String> previousQuestions;

    private List<String> previousExplanations;
}

