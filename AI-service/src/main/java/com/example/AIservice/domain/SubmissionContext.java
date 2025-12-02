package com.example.AIservice.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubmissionContext {
    Long studentId;
    Long questionId;
    String questionText;
    String studentAnswer;
    String correctAnswer;
    String topic;
    String difficulty;
    String subject;
}
