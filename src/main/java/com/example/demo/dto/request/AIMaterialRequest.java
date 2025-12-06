package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;

public class AIMaterialRequest {
    @NotNull
    private String studentId;

    @NotNull
    private String courseId;

    @NotNull
    private String studentPrompt;
}
