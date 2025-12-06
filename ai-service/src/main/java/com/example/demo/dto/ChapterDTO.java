package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChapterDTO {
    private String courseId;
    private String title;
    private int orderIndex;
    private DifficultyLevel difficulty;

    public enum DifficultyLevel {
        EASY, MEDIUM, HARD
    }
}

