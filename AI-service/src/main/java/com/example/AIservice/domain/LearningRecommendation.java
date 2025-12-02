package com.example.AIservice.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LearningRecommendation {
    Long studentId;
    String nextTopic;
    String explanation;
}

