package com.example.course.service.dto;

import lombok.Data;

@Data
public class MaterialDTO {
    private String title;
    private String type; // VIDEO, TEXT, INTERACTIVE
    private String contentOrUrl; // Field dùng chung để map vào videoUrl hoặc contentBody
    private String metadata;
}
