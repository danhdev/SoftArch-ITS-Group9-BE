package com.example.its.aifeedback.service;

import com.example.its.aifeedback.dto.HintRequestDTO;
import com.example.its.aifeedback.dto.HintResponseDTO;

import java.util.List;

public interface AIHintService {

    HintResponseDTO generateHint(HintRequestDTO requestDTO);

    List<HintResponseDTO> getHintHistory(Long studentId, Long questionId);

    List<HintResponseDTO> getStudentHintHistory(Long studentId);
}
