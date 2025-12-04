package com.example.course.service.dto;

import com.example.course.service.model.chapter.DifficultyLevel;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChapterDTO {
    String courseId;
    String title;
    int orderIndex;
    DifficultyLevel difficulty;
}
