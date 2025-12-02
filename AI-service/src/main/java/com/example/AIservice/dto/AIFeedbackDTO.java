package com.example.AIservice.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AIFeedbackDTO {
    Long id;
    Long studentId;
    Long questionId;
    String feedbackText;
    String hint;
}

