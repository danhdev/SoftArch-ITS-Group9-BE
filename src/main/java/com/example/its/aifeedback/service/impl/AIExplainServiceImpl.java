package com.example.its.aifeedback.service.impl;

import com.example.its.aifeedback.domain.AIExplanation;
import com.example.its.aifeedback.domain.ExplainSubmissionContext;
import com.example.its.aifeedback.dto.AIExplainRequestDTO;
import com.example.its.aifeedback.dto.AIExplainResponseDTO;
import com.example.its.aifeedback.dto.MaterialContentResponseDTO;
import com.example.its.aifeedback.dto.ResponseObject;
import com.example.its.aifeedback.engine.AIEngine;
import com.example.its.aifeedback.mapper.ExplainMapper;
import com.example.its.aifeedback.repository.AIExplanationRepository;
import com.example.its.aifeedback.service.AIExplainService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AIExplainServiceImpl implements AIExplainService {

    private final AIExplanationRepository explanationRepository;
    private final AIEngine aiEngine;
    private final RestTemplate restTemplate;

    @Value("${course.service.url:http://localhost:8081}")
    private String courseServiceUrl;

    public AIExplainServiceImpl(
            AIExplanationRepository explanationRepository,
            AIEngine aiEngine,
            RestTemplate restTemplate) {
        this.explanationRepository = explanationRepository;
        this.aiEngine = aiEngine;
        this.restTemplate = restTemplate;
    }

    @Override
    public AIExplainResponseDTO generateExplanation(AIExplainRequestDTO requestDTO) {
        List<AIExplanation> previousExplanations = getPreviousExplanations(
                requestDTO.getStudentId(),
                requestDTO.getMaterialId()
        );

        List<String> previousQuestions = previousExplanations.stream()
                .map(AIExplanation::getStudentQuestion)
                .collect(Collectors.toList());

        List<String> previousAnswers = previousExplanations.stream()
                .map(AIExplanation::getExplanation)
                .collect(Collectors.toList());

        MaterialContentResponseDTO materialContentData = fetchMaterialContent(requestDTO.getMaterialId());

        ExplainSubmissionContext context = ExplainMapper.toContext(
                requestDTO,
                materialContentData != null ? materialContentData.getContent() : null,
                materialContentData != null ? materialContentData.getFileName() : null,
                materialContentData != null ? materialContentData.getPages() : null,
                previousQuestions,
                previousAnswers
        );

        String generatedExplanation = aiEngine.generateExplanation(context);

        AIExplanation explanationEntity = AIExplanation.builder()
                .studentId(requestDTO.getStudentId())
                .materialId(requestDTO.getMaterialId())
                .studentQuestion(requestDTO.getStudentQuestion())
                .explanation(generatedExplanation)
                .build();

        AIExplanation savedExplanation = explanationRepository.save(explanationEntity);

        return ExplainMapper.toDTO(savedExplanation);
    }

    @Override
    public List<AIExplainResponseDTO> getExplainHistory(Long studentId, Long materialId) {
        List<AIExplanation> explanations = explanationRepository
                .findByStudentIdAndMaterialIdOrderByCreatedAtAsc(studentId, materialId);

        return explanations.stream()
                .map(ExplainMapper::toDTO)
                .collect(Collectors.toList());
    }

    private List<AIExplanation> getPreviousExplanations(Long studentId, Long materialId) {
        return explanationRepository.findByStudentIdAndMaterialIdOrderByCreatedAtAsc(studentId, materialId);
    }

    private MaterialContentResponseDTO fetchMaterialContent(Long materialId) {
        // TODO: Uncomment when course service URL is available
        /*
        try {
            String url = String.format("%s/materials/%d/content", courseServiceUrl, materialId);

            ResponseEntity<ResponseObject<MaterialContentResponseDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ResponseObject<MaterialContentResponseDTO>>() {}
            );

            if (response.getBody() != null && response.getBody().getData() != null) {
                return response.getBody().getData();
            }

            return null;
        } catch (Exception e) {
            System.err.println("Failed to fetch material content: " + e.getMessage());
            return null;
        }
        */

        // Mock data for testing - Mathematics: Derivatives of Quadratic Functions
        MaterialContentResponseDTO mockData = new MaterialContentResponseDTO();
        mockData.setFileName("Chương 3 - Đạo Hàm Hàm Số.pdf");
        mockData.setPages("45-58");
        mockData.setContent(
                "CHƯƠNG 3: ĐẠO HÀM HÀM SỐ\n\n" +
                "3.1. KHÁI NIỆM ĐẠO HÀM\n" +
                "Đạo hàm của hàm số y = f(x) tại điểm x₀ là giới hạn (nếu tồn tại):\n" +
                "f'(x₀) = lim[h→0] (f(x₀+h) - f(x₀))/h\n\n" +
                "Ý nghĩa:\n" +
                "- Đạo hàm biểu thị tốc độ thay đổi tức thời của hàm số\n" +
                "- Hệ số góc của tiếp tuyến tại điểm đó\n\n" +
                "3.2. CÁC QUY TẮC TÍNH ĐẠO HÀM CỞ BẢN\n\n" +
                "Quy tắc 1: Đạo hàm của hằng số\n" +
                "(c)' = 0, với c là hằng số\n" +
                "Ví dụ: (5)' = 0, (π)' = 0\n\n" +
                "Quy tắc 2: Đạo hàm của x^n\n" +
                "(x^n)' = n·x^(n-1)\n" +
                "Ví dụ:\n" +
                "- (x)' = 1\n" +
                "- (x²)' = 2x\n" +
                "- (x³)' = 3x²\n" +
                "- (x⁴)' = 4x³\n\n" +
                "Quy tắc 3: Đạo hàm của tổng/hiệu\n" +
                "(u ± v)' = u' ± v'\n" +
                "Ví dụ: (x² + 3x)' = (x²)' + (3x)' = 2x + 3\n\n" +
                "Quy tắc 4: Đạo hàm của tích với hằng số\n" +
                "(c·u)' = c·u'\n" +
                "Ví dụ: (3x)' = 3·(x)' = 3·1 = 3\n\n" +
                "3.3. ĐẠO HÀM CỦA HÀM SỐ BẬC HAI\n\n" +
                "Hàm số bậc hai có dạng tổng quát: f(x) = ax² + bx + c\n" +
                "Trong đó: a ≠ 0, a, b, c là các hằng số\n\n" +
                "Cách tính đạo hàm:\n" +
                "Bước 1: Áp dụng quy tắc đạo hàm của tổng\n" +
                "f'(x) = (ax²)' + (bx)' + (c)'\n\n" +
                "Bước 2: Tính đạo hàm từng số hạng\n" +
                "- (ax²)' = a·(x²)' = a·2x = 2ax\n" +
                "- (bx)' = b·(x)' = b·1 = b\n" +
                "- (c)' = 0\n\n" +
                "Bước 3: Kết hợp các kết quả\n" +
                "f'(x) = 2ax + b\n\n" +
                "VÍ DỤ MINH HỌA:\n\n" +
                "Ví dụ 1: Tính đạo hàm của f(x) = x² + 3x + 2\n" +
                "Giải:\n" +
                "f'(x) = (x²)' + (3x)' + (2)'\n" +
                "     = 2x + 3 + 0\n" +
                "     = 2x + 3\n\n" +
                "Ví dụ 2: Tính đạo hàm của f(x) = 2x² - 5x + 1\n" +
                "Giải:\n" +
                "f'(x) = (2x²)' + (-5x)' + (1)'\n" +
                "     = 2·2x + (-5)·1 + 0\n" +
                "     = 4x - 5\n\n" +
                "Ví dụ 3: Tính đạo hàm của f(x) = -x² + 4x - 7\n" +
                "Giải:\n" +
                "f'(x) = (-x²)' + (4x)' + (-7)'\n" +
                "     = -2x + 4 + 0\n" +
                "     = -2x + 4\n\n" +
                "LƯU Ý QUAN TRỌNG:\n" +
                "1. Đạo hàm của hàm bậc hai luôn là hàm bậc nhất (đường thẳng)\n" +
                "2. Hệ số a trong hàm gốc quyết định hệ số góc 2a trong đạo hàm\n" +
                "3. Hằng số c không ảnh hưởng đến đạo hàm (vì đạo hàm của hằng số = 0)\n" +
                "4. Đạo hàm cho biết tốc độ tăng/giảm của hàm số:\n" +
                "   - f'(x) > 0: hàm số đồng biến\n" +
                "   - f'(x) < 0: hàm số nghịch biến\n" +
                "   - f'(x) = 0: điểm cực trị\n\n" +
                "BÀI TẬP THỰC HÀNH:\n" +
                "1. Tính đạo hàm: f(x) = 3x² + 2x - 5\n" +
                "2. Tính đạo hàm: g(x) = -2x² + 6x + 1\n" +
                "3. Tính đạo hàm: h(x) = x² - x + 10\n" +
                "4. Tìm đạo hàm tại x = 2 của f(x) = x² + 3x + 2\n\n" +
                "ỨNG DỤNG:\n" +
                "- Tìm cực trị của hàm số\n" +
                "- Xác định tính đồng biến, nghịch biến\n" +
                "- Vẽ đồ thị hàm số\n" +
                "- Bài toán tối ưu hóa trong thực tế"
        );

        return mockData;
    }
}

