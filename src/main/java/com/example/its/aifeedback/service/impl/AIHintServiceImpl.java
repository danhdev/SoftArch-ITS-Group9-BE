package com.example.its.aifeedback.service.impl;

import com.example.its.aifeedback.domain.AIHint;
import com.example.its.aifeedback.domain.HintSubmissionContext;
import com.example.its.aifeedback.dto.HintRequestDTO;
import com.example.its.aifeedback.dto.HintResponseDTO;
import com.example.its.aifeedback.dto.MaterialDTO;
import com.example.its.aifeedback.dto.ResponseObject;
import com.example.its.aifeedback.engine.AIEngine;
import com.example.its.aifeedback.mapper.HintMapper;
import com.example.its.aifeedback.repository.AIHintRepository;
import com.example.its.aifeedback.service.AIHintService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AIHintServiceImpl implements AIHintService {

    private final AIHintRepository hintRepository;
    private final AIEngine aiEngine;
    private final RestTemplate restTemplate;

    @Value("${course.service.url:http://localhost:8081}")
    private String courseServiceUrl;

    public AIHintServiceImpl(
            AIHintRepository hintRepository,
            AIEngine aiEngine,
            RestTemplate restTemplate) {
        this.hintRepository = hintRepository;
        this.aiEngine = aiEngine;
        this.restTemplate = restTemplate;
    }

    @Override
    public HintResponseDTO generateHint(HintRequestDTO requestDTO) {
        List<String> previousHints = getPreviousHints(requestDTO.getStudentId(), requestDTO.getQuestionId());

        List<MaterialDTO> materials = fetchCourseMaterials(requestDTO.getSubjectId());

        HintSubmissionContext context = HintMapper.toContext(requestDTO, previousHints, materials);

        String generatedHint = aiEngine.generateHint(context);

        AIHint hintEntity = AIHint.builder()
                .studentId(requestDTO.getStudentId())
                .questionId(requestDTO.getQuestionId())
                .hint(generatedHint)
                .subjectId(requestDTO.getSubjectId())
                .subject(requestDTO.getSubject())
                .topic(requestDTO.getTopic())
                .difficulty(requestDTO.getDifficulty())
                .build();

        AIHint savedHint = hintRepository.save(hintEntity);

        Integer hintCount = previousHints.size() + 1;

        return HintMapper.toDTO(savedHint, hintCount);
    }

    @Override
    public List<HintResponseDTO> getHintHistory(Long studentId, Long questionId) {
        List<AIHint> hints = hintRepository.findByStudentIdAndQuestionIdOrderByCreatedAtAsc(studentId, questionId);
        List<HintResponseDTO> hintDTOs = new java.util.ArrayList<>();

        for (int i = 0; i < hints.size(); i++) {
            hintDTOs.add(HintMapper.toDTO(hints.get(i), i + 1));
        }

        return hintDTOs;
    }

    private List<String> getPreviousHints(Long studentId, Long questionId) {
        List<AIHint> previousHints = hintRepository.findByStudentIdAndQuestionIdOrderByCreatedAtAsc(studentId, questionId);
        return previousHints.stream()
                .map(AIHint::getHint)
                .collect(Collectors.toList());
    }

    private List<MaterialDTO> fetchCourseMaterials(String courseId) {
        // TODO: Uncomment when course service URL is available
        /*
        try {
            String url = String.format("%s/materials/%s", courseServiceUrl, courseId);

            ResponseEntity<ResponseObject<List<MaterialDTO>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ResponseObject<List<MaterialDTO>>>() {}
            );

            if (response.getBody() != null && response.getBody().getData() != null) {
                return response.getBody().getData();
            }

            return Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Failed to fetch course materials: " + e.getMessage());
            return Collections.emptyList();
        }
        */

        // Mock data for testing - Mathematics/Calculus materials
        List<MaterialDTO> mockMaterials = List.of(
                MaterialDTO.builder()
                        .title("Khái niệm Đạo hàm và Ý nghĩa Hình học")
                        .type("PDF")
                        .contentOrUrl("Đạo hàm của hàm số y = f(x) tại điểm x₀ là giới hạn: f'(x₀) = lim[h→0] (f(x₀+h) - f(x₀))/h. " +
                                "Các quy tắc tính đạo hàm cơ bản: (xⁿ)' = n·xⁿ⁻¹, (ax)' = a, (c)' = 0. " +
                                "Quy tắc cộng: (u + v)' = u' + v'. Quy tắc nhân với hằng số: (cu)' = c·u'. " +
                                "Ví dụ: f(x) = x² có đạo hàm f'(x) = 2x, f(x) = 3x có đạo hàm f'(x) = 3.")
                        .metadata("Chương 3: Đạo hàm, Trang 45-58")
                        .build(),
                MaterialDTO.builder()
                        .title("Các Quy tắc Tính Đạo hàm Hàm Đa thức")
                        .type("Video")
                        .contentOrUrl("https://example.com/video/dao-ham-da-thuc - Video hướng dẫn chi tiết cách tính đạo hàm của hàm đa thức. " +
                                "Giải thích từng bước: Với hàm f(x) = axⁿ + bxᵐ + ... + k, ta áp dụng quy tắc đạo hàm từng số hạng. " +
                                "Ví dụ minh họa: f(x) = x² + 3x + 2, áp dụng (x²)' = 2x, (3x)' = 3, (2)' = 0, ta được f'(x) = 2x + 3. " +
                                "Lưu ý: Đạo hàm của hằng số luôn bằng 0, đạo hàm của x^n là n·x^(n-1).")
                        .metadata("Thời lượng: 15 phút, Độ khó: Trung bình")
                        .build(),
                MaterialDTO.builder()
                        .title("Bài tập Thực hành Tính Đạo hàm")
                        .type("Document")
                        .contentOrUrl("Tập hợp các bài tập về tính đạo hàm hàm đa thức có lời giải chi tiết. " +
                                "Bài 1: Tính đạo hàm f(x) = x³ + 2x² - 5x + 1 → Đáp án: f'(x) = 3x² + 4x - 5. " +
                                "Bài 2: Tính đạo hàm f(x) = 4x² - 3x + 7 → Đáp án: f'(x) = 8x - 3. " +
                                "Bài 3: Tính đạo hàm f(x) = x² + 3x + 2 → Đáp án: f'(x) = 2x + 3. " +
                                "Mỗi bài có hướng dẫn từng bước và giải thích cách áp dụng công thức.")
                        .metadata("Cấp độ: Cơ bản đến Trung bình, 25 bài tập")
                        .build()
        );

        return mockMaterials;
    }
}

