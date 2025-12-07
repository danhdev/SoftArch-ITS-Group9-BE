package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for material content response from course service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialContentResponseDTO {
    private String fileName;
    private String pages;
    private String content;
}

