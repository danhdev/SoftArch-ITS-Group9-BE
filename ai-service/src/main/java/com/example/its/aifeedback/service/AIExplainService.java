package com.example.its.aifeedback.service;

import com.example.its.aifeedback.dto.AIExplainRequestDTO;
import com.example.its.aifeedback.dto.AIExplainResponseDTO;

import java.util.List;

public interface AIExplainService {

    AIExplainResponseDTO generateExplanation(AIExplainRequestDTO requestDTO);

    List<AIExplainResponseDTO> getExplainHistory(Long studentId, Long materialId);
}
