package com.example.demo.dto.material;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO containing all course materials including chapters and their materials.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseMaterialsDTO {
    private String courseId;
    private List<ChapterDTO> chapters;
    private List<MaterialDTO> materials;
}
