package com.example.demo.services.prompt.context;

import com.example.demo.dto.material.ChapterDTO;
import com.example.demo.dto.material.MaterialDTO;
import com.example.demo.dto.request.AIMaterialRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Context data for building material recommendation prompts.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialRecommendationPromptContext {
    private AIMaterialRequest request;
    private List<ChapterDTO> chapters;
    private List<MaterialDTO> materials;
}
