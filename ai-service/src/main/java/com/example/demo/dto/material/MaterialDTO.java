package com.example.demo.dto.material;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a material item within a chapter.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialDTO {
    private String title;
    private String type;
    private String contentOrUrl;
    private String metadata;
}
