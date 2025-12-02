package com.example.AIservice.service;

import com.example.AIservice.dto.AIFeedbackDTO;
import java.util.List;

public interface AIFeedbackQueryService {
    List<AIFeedbackDTO> getFeedbackHistory(Long studentId);
    AIFeedbackDTO getLatestFeedback(Long studentId);
}
