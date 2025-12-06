package com.example.demo.services.hint;

import org.springframework.stereotype.Service;

import com.example.demo.dto.request.AIHintRequest;
import com.example.demo.dto.AIResponse;
import com.example.demo.dto.HintResponseDTO;
import com.example.demo.models.AIHint;
import com.example.demo.repository.AIHintRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of HintService.
 * Follows Single Responsibility Principle - handles only hint generation operations.
 * Uses Strategy Pattern via AI Tasks.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HintServiceImpl implements HintService {

    private final HintGenerationTask hintGenerationTask;
    private final AIHintRepository hintRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public AIResponse hint(AIHintRequest request) {
        log.info("Processing hint generation request for course: {}, assessment: {}, question: {}",
                request.getCourseId(), request.getAssessmentId(), request.getQuestionId());

        System.out.println("HintServiceImpl: Delegating to HintGenerationTask");

        return hintGenerationTask.execute(request);
    }

    @Override
    public List<HintResponseDTO> getHintHistory(Long studentId, Long questionId) {
        log.info("Fetching hint history for student: {}, question: {}", studentId, questionId);

        List<AIHint> hints = hintRepository.findByStudentIdAndQuestionIdOrderByCreatedAtAsc(studentId, questionId);
        List<HintResponseDTO> hintDTOs = new ArrayList<>();

        for (int i = 0; i < hints.size(); i++) {
            AIHint hint = hints.get(i);
            HintResponseDTO dto = HintResponseDTO.builder()
                    .hintId(hint.getId())
                    .questionId(hint.getQuestionId())
                    .studentId(hint.getStudentId())
                    .hint(hint.getHint())
                    .hintCount(i + 1)
                    .createdAt(hint.getCreatedAt() != null ? hint.getCreatedAt().format(DATE_FORMATTER) : null)
                    .build();
            hintDTOs.add(dto);
        }

        log.info("Found {} hints for student: {}, question: {}", hintDTOs.size(), studentId, questionId);

        return hintDTOs;
    }
}
