package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for hint information.
 * Used by HintController to return hint generation results and history.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HintResponseDTO {
    
    private Long hintId;
    
    private Long questionId;
    
    private Long studentId;
    
    private String hint;
    
    private Integer hintCount;
    
    private String createdAt;
}
