package com.example.course.service.dto;

import org.springframework.stereotype.Component;

import com.example.course.service.model.chapter.DifficultyLevel;

import lombok.AllArgsConstructor;
import lombok.Data;

@Component
@Data
@AllArgsConstructor
public class ChapterDTO {
    String courseId;
    String title;
    int orderIndex;
    DifficultyLevel difficulty;
}
