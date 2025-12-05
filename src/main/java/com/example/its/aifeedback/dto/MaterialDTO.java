package com.example.its.aifeedback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialDTO {
    private String contentOrUrl;
    private String metadata;
    private String title;
    private String type;
}
