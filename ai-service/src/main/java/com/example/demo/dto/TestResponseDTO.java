package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO representing the test/assessment response from the external API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestResponseDTO {

    private Long courseId;

    private Long testId;

    private Long studentId;

    private String title;

    private String description;

    private String dateTaken;

    private String timeStart;

    private String timeLimit;

    private List<QuestionDTO> questionList;
}
