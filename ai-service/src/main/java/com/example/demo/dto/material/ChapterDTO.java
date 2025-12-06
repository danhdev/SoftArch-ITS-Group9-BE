package com.example.demo.dto.material;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a chapter in a course.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChapterDTO {
    private String chapterId;
    private String courseId;
    private String title;
    private Integer orderIndex;
    private String difficulty;
}
