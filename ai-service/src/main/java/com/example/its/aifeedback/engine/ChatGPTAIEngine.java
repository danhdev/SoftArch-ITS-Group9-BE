package com.example.its.aifeedback.engine;

import com.example.its.aifeedback.domain.AIFeedback;
import com.example.its.aifeedback.domain.ExplainSubmissionContext;
import com.example.its.aifeedback.domain.HintSubmissionContext;
import com.example.its.aifeedback.domain.LearningRecommendation;
import com.example.its.aifeedback.domain.SubmissionContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ================================
 * STRATEGY PATTERN - ChatGPT Implementation
 * ================================
 * 
 * Implementation của AIEngine sử dụng OpenAI ChatGPT API.
 * 
 * @Primary annotation đánh dấu đây là implementation được ưu tiên inject
 *          khi có nhiều bean cùng implement AIEngine interface.
 * 
 *          Cần config trong application.properties hoặc .env:
 *          - OPENAI_API_KEY=sk-xxx
 *          - OPENAI_MODEL=gpt-3.5-turbo (hoặc gpt-4)
 */
@Component
@Primary // Ưu tiên sử dụng ChatGPT thay vì SimpleRuleBasedAIEngine
public class ChatGPTAIEngine implements AIEngine {

    private static final Logger logger = LoggerFactory.getLogger(ChatGPTAIEngine.class);

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    @Value("${openai.api.key:}")
    private String apiKey;

    @Value("${openai.model:gpt-3.5-turbo}")
    private String model;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // Fallback engine khi API không available
    private final SimpleRuleBasedAIEngine fallbackEngine;

    public ChatGPTAIEngine() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.fallbackEngine = new SimpleRuleBasedAIEngine();
    }

    /**
     * Sinh feedback sử dụng ChatGPT API.
     * Nếu API key không có hoặc call API fail -> fallback về rule-based engine.
     */
    @Override
    public AIFeedback generateFeedback(SubmissionContext ctx) {
        // Kiểm tra API key
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your-openai-api-key-here")) {
            logger.warn("OpenAI API key not configured. Using fallback rule-based engine.");
            return fallbackEngine.generateFeedback(ctx);
        }

        try {
            // Tạo prompt cho ChatGPT
            String prompt = buildFeedbackPrompt(ctx);

            // Gọi ChatGPT API
            String response = callChatGPT(prompt);

            // Parse response và tạo AIFeedback
            return parseFeedbackResponse(response, ctx);

        } catch (Exception e) {
            logger.error("Error calling ChatGPT API: {}. Using fallback.", e.getMessage());
            return fallbackEngine.generateFeedback(ctx);
        }
    }

    @Override
    public String generateHint(HintSubmissionContext ctx) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your-openai-api-key-here")) {
            logger.warn("OpenAI API key not configured. Using fallback rule-based engine.");
            return fallbackEngine.generateHint(ctx);
        }

        try {
            String prompt = buildHintPrompt(ctx);
            return callChatGPT(prompt);
        } catch (Exception e) {
            logger.error("Error calling ChatGPT API: {}. Using fallback.", e.getMessage());
            return fallbackEngine.generateHint(ctx);
        }
    }

    @Override
    public String generateExplanation(ExplainSubmissionContext ctx) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your-openai-api-key-here")) {
            logger.warn("OpenAI API key not configured. Using fallback rule-based engine.");
            return fallbackEngine.generateExplanation(ctx);
        }

        try {
            String prompt = buildExplanationPrompt(ctx);
            return callChatGPT(prompt);
        } catch (Exception e) {
            logger.error("Error calling ChatGPT API for explanation: {}. Using fallback.", e.getMessage());
            return fallbackEngine.generateExplanation(ctx);
        }
    }

    /**
     * Sinh learning recommendations sử dụng ChatGPT API.
     * Version cơ bản - không có dữ liệu phân tích.
     */
    @Override
    public List<LearningRecommendation> suggestNextSteps(Long studentId) {
        // Delegate to enhanced version with empty data
        return suggestNextSteps(studentId, 0, 0, List.of(), List.of(), List.of());
    }

    /**
     * Sinh learning recommendations với dữ liệu phân tích chi tiết.
     * 
     * TIÊU CHÍ ĐỀ XUẤT:
     * 1. Nếu accuracy < 50%: Ưu tiên ôn lại kiến thức cơ bản
     * 2. Nếu có weak topics: Đề xuất củng cố các topic yếu trước
     * 3. Nếu accuracy 50-80%: Cân bằng giữa củng cố yếu và nâng cao
     * 4. Nếu accuracy > 80%: Đề xuất thử thách mới, chủ đề nâng cao
     * 5. Xem xét xu hướng gần đây: Đang tiến bộ hay đi xuống?
     */
    @Override
    public List<LearningRecommendation> suggestNextSteps(
            Long studentId,
            long totalAttempts,
            long correctCount,
            List<Object[]> strongTopics,
            List<Object[]> weakTopics,
            List<AIFeedback> recentHistory) {

        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your-openai-api-key-here")) {
            logger.warn("OpenAI API key not configured. Using fallback rule-based engine.");
            return generateFallbackRecommendations(studentId, totalAttempts, correctCount, strongTopics, weakTopics);
        }

        try {
            String prompt = buildEnhancedRecommendationPrompt(
                    studentId, totalAttempts, correctCount, strongTopics, weakTopics, recentHistory);
            String response = callChatGPT(prompt);
            return parseRecommendationResponse(response, studentId);

        } catch (Exception e) {
            logger.error("Error calling ChatGPT API: {}. Using fallback.", e.getMessage());
            return generateFallbackRecommendations(studentId, totalAttempts, correctCount, strongTopics, weakTopics);
        }
    }

    /**
     * Tạo recommendations mặc định khi không có ChatGPT.
     */
    private List<LearningRecommendation> generateFallbackRecommendations(
            Long studentId,
            long totalAttempts,
            long correctCount,
            List<Object[]> strongTopics,
            List<Object[]> weakTopics) {

        List<LearningRecommendation> recommendations = new ArrayList<>();

        // Nếu chưa có data
        if (totalAttempts == 0) {
            recommendations.add(LearningRecommendation.builder()
                    .studentId(studentId)
                    .nextTopic("Bắt đầu học")
                    .explanation("👋 Chào mừng bạn! Hãy bắt đầu làm một số bài tập để hệ thống phân tích năng lực của bạn.")
                    .build());
            return recommendations;
        }

        double accuracy = totalAttempts > 0 ? (correctCount * 100.0 / totalAttempts) : 0;

        // Dựa trên accuracy và weak topics
        if (accuracy < 50 && !weakTopics.isEmpty()) {
            String weakTopic = (String) weakTopics.get(0)[0];
            recommendations.add(LearningRecommendation.builder()
                    .studentId(studentId)
                    .nextTopic("Ôn tập: " + weakTopic)
                    .explanation(String.format("📚 Bạn đang có %.1f%% chính xác. Hãy ôn lại kiến thức về \"%s\" - đây là chủ đề bạn cần cải thiện nhất.", accuracy, weakTopic))
                    .build());
        } else if (accuracy >= 50 && accuracy < 80) {
            if (!weakTopics.isEmpty()) {
                String weakTopic = (String) weakTopics.get(0)[0];
                recommendations.add(LearningRecommendation.builder()
                        .studentId(studentId)
                        .nextTopic("Củng cố: " + weakTopic)
                        .explanation(String.format("💪 Tốt lắm! Độ chính xác %.1f%%. Hãy củng cố thêm \"%s\" để nâng cao hơn nữa.", accuracy, weakTopic))
                        .build());
            }
        } else if (accuracy >= 80 && !strongTopics.isEmpty()) {
            String strongTopic = (String) strongTopics.get(0)[0];
            recommendations.add(LearningRecommendation.builder()
                    .studentId(studentId)
                    .nextTopic("Nâng cao: " + strongTopic)
                    .explanation(String.format("🌟 Xuất sắc! Độ chính xác %.1f%%. Bạn có thể thử các bài tập nâng cao về \"%s\".", accuracy, strongTopic))
                    .build());
        }

        // Thêm recommendation chung
        recommendations.add(LearningRecommendation.builder()
                .studentId(studentId)
                .nextTopic("Thực hành đều đặn")
                .explanation("📆 Hãy luyện tập mỗi ngày 15-30 phút để duy trì và nâng cao kỹ năng!")
                .build());

        return recommendations;
    }

    /**
     * Xây dựng prompt với đầy đủ dữ liệu phân tích.
     */
    private String buildEnhancedRecommendationPrompt(
            Long studentId,
            long totalAttempts,
            long correctCount,
            List<Object[]> strongTopics,
            List<Object[]> weakTopics,
            List<AIFeedback> recentHistory) {

        double accuracy = totalAttempts > 0 ? (correctCount * 100.0 / totalAttempts) : 0;

        // Build strong topics string
        StringBuilder strongStr = new StringBuilder();
        for (Object[] row : strongTopics) {
            if (row[0] != null) {
                strongStr.append(String.format("  - %s: %d câu đúng\n", row[0], ((Number) row[1]).longValue()));
            }
        }
        if (strongStr.length() == 0) strongStr.append("  (Chưa có dữ liệu)\n");

        // Build weak topics string
        StringBuilder weakStr = new StringBuilder();
        for (Object[] row : weakTopics) {
            if (row[0] != null) {
                weakStr.append(String.format("  - %s: %d câu sai\n", row[0], ((Number) row[1]).longValue()));
            }
        }
        if (weakStr.length() == 0) weakStr.append("  (Chưa có dữ liệu)\n");

        // Build recent history string
        StringBuilder historyStr = new StringBuilder();
        int recentCorrect = 0;
        for (AIFeedback fb : recentHistory) {
            if (fb.getIsCorrect() != null && fb.getIsCorrect()) recentCorrect++;
            historyStr.append(String.format("  - %s (%s): %s\n",
                    fb.getTopic() != null ? fb.getTopic() : "N/A",
                    fb.getDifficulty() != null ? fb.getDifficulty() : "N/A",
                    fb.getIsCorrect() != null && fb.getIsCorrect() ? "✓ Đúng" : "✗ Sai"));
        }
        double recentAccuracy = recentHistory.size() > 0 ? (recentCorrect * 100.0 / recentHistory.size()) : 0;

        return String.format("""
                Bạn là một cố vấn học tập AI trong hệ thống Intelligent Tutoring System.
                Dựa trên dữ liệu phân tích sau, hãy đề xuất 2-3 bước học tập tiếp theo cho học sinh.

                === DỮ LIỆU HỌC SINH (ID: %d) ===

                📊 THỐNG KÊ TỔNG QUAN:
                - Tổng số bài đã làm: %d
                - Số câu trả lời đúng: %d
                - Độ chính xác tổng: %.1f%%

                💪 CÁC CHỦ ĐỀ MẠNH (làm đúng nhiều):
                %s
                📈 CÁC CHỦ ĐỀ CẦN CẢI THIỆN (làm sai nhiều):
                %s
                🕐 LỊCH SỬ GẦN ĐÂY (%d bài, accuracy: %.1f%%):
                %s

                === YÊU CẦU ===

                Dựa trên các tiêu chí sau để đề xuất:
                1. Nếu accuracy < 50%%: Ưu tiên ôn lại kiến thức cơ bản của chủ đề yếu nhất
                2. Nếu accuracy 50-80%%: Củng cố chủ đề yếu + giới thiệu bài khó hơn ở chủ đề mạnh
                3. Nếu accuracy > 80%%: Thử thách với bài nâng cao hoặc chủ đề mới
                4. Xem xu hướng gần đây: Đang tiến bộ (khuyến khích) hay đi xuống (cần điều chỉnh)?

                Trả về JSON array với format:
                [
                    {
                        "nextTopic": "Tên chủ đề cụ thể",
                        "explanation": "Giải thích TẠI SAO đề xuất chủ đề này dựa trên dữ liệu (2-3 câu)"
                    }
                ]

                Lưu ý:
                - Đề xuất PHẢI dựa trên dữ liệu thực tế ở trên
                - Giải thích cần nhắc đến số liệu cụ thể (vd: "Bạn đang có 65%% accuracy...")
                - Sử dụng tiếng Việt, thân thiện với emoji
                - Tối đa 3 recommendations
                """,
                studentId,
                totalAttempts,
                correctCount,
                accuracy,
                strongStr.toString(),
                weakStr.toString(),
                recentHistory.size(),
                recentAccuracy,
                historyStr.toString());
    }

    // ========== Private Helper Methods ==========

    private String buildHintPrompt(HintSubmissionContext ctx) {
        int previousHintCount = ctx.getPreviousHints() != null ? ctx.getPreviousHints().size() : 0;
        String hintLevel = "cơ bản";

        if (previousHintCount == 0) {
            hintLevel = "chung chung, chỉ gợi ý hướng suy nghĩ";
        } else if (previousHintCount == 1) {
            hintLevel = "rõ ràng hơn, chỉ ra phương pháp giải";
        } else if (previousHintCount >= 2) {
            hintLevel = "chi tiết hơn, hướng dẫn từng bước";
        }

        StringBuilder previousHintsText = new StringBuilder();
        if (ctx.getPreviousHints() != null && !ctx.getPreviousHints().isEmpty()) {
            previousHintsText.append("\n\nCác gợi ý đã cung cấp trước đó cho học sinh:\n");
            for (int i = 0; i < ctx.getPreviousHints().size(); i++) {
                previousHintsText.append(String.format("%d. %s\n", i + 1, ctx.getPreviousHints().get(i)));
            }
            previousHintsText.append("\nHọc sinh vẫn chưa tìm ra câu trả lời, hãy đưa ra gợi ý mới dựa trên những gợi ý trước đó.");
        }

        StringBuilder materialsText = new StringBuilder();
        if (ctx.getMaterials() != null && !ctx.getMaterials().isEmpty()) {
            materialsText.append("\n\nTÀI LIỆU KHÓA HỌC LIÊN QUAN:\n");
            for (int i = 0; i < ctx.getMaterials().size(); i++) {
                var material = ctx.getMaterials().get(i);
                materialsText.append(String.format("%d. ", i + 1));

                if (material.getTitle() != null && !material.getTitle().isEmpty()) {
                    materialsText.append(String.format("Tiêu đề: %s", material.getTitle()));
                }

                if (material.getType() != null && !material.getType().isEmpty()) {
                    materialsText.append(String.format(" (Loại: %s)", material.getType()));
                }

                materialsText.append("\n");

                if (material.getContentOrUrl() != null && !material.getContentOrUrl().isEmpty()) {
                    String content = material.getContentOrUrl();
//                    if (content.length() > 200) {
//                        content = content.substring(0, 200) + "...";
//                    }
                    materialsText.append(String.format("   Nội dung: %s\n", content));
                }

                if (material.getMetadata() != null && !material.getMetadata().isEmpty()) {
                    materialsText.append(String.format("   Metadata: %s\n", material.getMetadata()));
                }
            }
            materialsText.append("\nHãy tham khảo tài liệu trên để đưa ra gợi ý phù hợp với nội dung khóa học.");
        }

        return String.format("""
                Bạn là một giáo viên AI thân thiện trong hệ thống Intelligent Tutoring System.
                Học sinh đang gặp khó khăn với câu hỏi và cần gợi ý.

                Thông tin liên quan đến câu hỏi:
                - Môn học: %s
                - Chủ đề: %s
                - Độ khó: %s
                - Đáp án: %s
                - Câu hỏi: %s%s%s

                Yêu cầu:
                - Đưa ra gợi ý ở mức độ: %s
                - KHÔNG tiết lộ đáp án trực tiếp
                - Gợi ý cần giúp học sinh tự tìm ra câu trả lời
                - Nếu có tài liệu khóa học, hãy dựa vào tài liệu đó để đưa ra gợi ý phù hợp
                - Nếu đây là gợi ý đầu tiên, chỉ gợi ý chung chung, không quá cụ thể
                - Nếu đã có gợi ý trước, hãy đưa ra gợi ý mới không trùng lặp và chi tiết hơn
                - Sử dụng tiếng Việt thân thiện với emoji phù hợp
                - Trả về chỉ nội dung gợi ý (không cần JSON)

                Gợi ý:
                """,
                ctx.getSubject() != null ? ctx.getSubject() : "Chưa xác định",
                ctx.getTopic() != null ? ctx.getTopic() : "Chưa xác định",
                ctx.getDifficulty() != null ? ctx.getDifficulty() : "Trung bình",
                ctx.getCorrectAnswer() != null ? ctx.getCorrectAnswer() : "Chưa cung cấp",
                ctx.getQuestionText(),
                previousHintsText.toString(),
                materialsText.toString(),
                hintLevel);
    }

    private String buildExplanationPrompt(ExplainSubmissionContext ctx) {
        StringBuilder materialContentText = new StringBuilder();
        if (ctx.getMaterialContent() != null && !ctx.getMaterialContent().isEmpty()) {
            materialContentText.append("\n📚 NỘI DUNG TÀI LIỆU:\n");

            if (ctx.getFileName() != null && !ctx.getFileName().isEmpty()) {
                materialContentText.append(String.format("Tên tài liệu: %s\n", ctx.getFileName()));
            }

            if (ctx.getPages() != null && !ctx.getPages().isEmpty()) {
                materialContentText.append(String.format("Số trang: %s\n", ctx.getPages()));
            }

            materialContentText.append("\nNội dung:\n");
            materialContentText.append(ctx.getMaterialContent());
            materialContentText.append("\n");
        }

        StringBuilder previousQAText = new StringBuilder();
        if (ctx.getPreviousQuestions() != null && !ctx.getPreviousQuestions().isEmpty() &&
            ctx.getPreviousExplanations() != null && !ctx.getPreviousExplanations().isEmpty()) {

            previousQAText.append("\n\n💬 CÁC CÂU HỎI VÀ GIẢI THÍCH TRƯỚC ĐÓ:\n");
            int count = Math.min(ctx.getPreviousQuestions().size(), ctx.getPreviousExplanations().size());

            for (int i = 0; i < count; i++) {
                previousQAText.append(String.format("\n--- Câu hỏi %d ---\n", i + 1));
                previousQAText.append(String.format("❓ Câu hỏi: %s\n", ctx.getPreviousQuestions().get(i)));
                previousQAText.append(String.format("💡 Giải thích: %s\n", ctx.getPreviousExplanations().get(i)));
            }

            previousQAText.append("\n⚠️ Học sinh vẫn chưa hiểu rõ tài liệu này và có câu hỏi mới. Hãy giải thích theo góc độ khác hoặc chi tiết hơn.");
        }

        return String.format("""
                Bạn là một giáo viên AI thân thiện trong hệ thống Intelligent Tutoring System.
                Học sinh đang học một tài liệu và có câu hỏi cần giải thích.

                %s%s

                ❓ CÂU HỎI CỦA HỌC SINH:
                %s

                YÊU CẦU:
                - Dựa vào NỘI DUNG TÀI LIỆU ở trên để giải thích
                - Giải thích dễ hiểu, phù hợp với trình độ học sinh
                - Nếu có câu hỏi trước đó, đừng lặp lại giải thích cũ mà hãy bổ sung thêm góc nhìn mới
                - Có thể đưa ra ví dụ minh họa để học sinh dễ hiểu hơn
                - Khuyến khích học sinh tự suy nghĩ và đặt câu hỏi tiếp
                - Sử dụng tiếng Việt thân thiện với emoji phù hợp
                - Trả về CHỈ nội dung giải thích (không cần JSON)

                GIẢI THÍCH:
                """,
                materialContentText.toString(),
                previousQAText.toString(),
                ctx.getStudentQuestion() != null ? ctx.getStudentQuestion() : "Chưa có câu hỏi");
    }

    /**
     * Xây dựng prompt để yêu cầu ChatGPT sinh feedback.
     */
    private String buildFeedbackPrompt(SubmissionContext ctx) {
        return String.format("""
                Bạn là một giáo viên AI thân thiện trong hệ thống Intelligent Tutoring System.
                Hãy đánh giá câu trả lời của học sinh và đưa ra feedback chi tiết bằng tiếng Việt.

                Thông tin bài làm:
                - Môn học: %s
                - Chủ đề: %s
                - Độ khó: %s
                - Câu hỏi: %s
                - Câu trả lời của học sinh: %s
                - Đáp án đúng: %s

                Yêu cầu trả về JSON với format:
                {
                    "feedbackText": "Nhận xét chi tiết về câu trả lời (2-3 câu)",
                    "hint": "Gợi ý học tập hoặc bước tiếp theo (1-2 câu)"
                }

                Lưu ý:
                - Nếu đúng: Khen ngợi và khuyến khích
                - Nếu sai: Động viên, chỉ ra chỗ sai và hướng dẫn cách khắc phục
                - Nếu đúng một phần: Ghi nhận phần đúng và hướng dẫn hoàn thiện
                - Sử dụng emoji phù hợp
                - Không tiết lộ đáp án đúng trực tiếp
                """,
                ctx.getSubject() != null ? ctx.getSubject() : "Chưa xác định",
                ctx.getTopic() != null ? ctx.getTopic() : "Chưa xác định",
                ctx.getDifficulty() != null ? ctx.getDifficulty() : "Trung bình",
                ctx.getQuestionText(),
                ctx.getStudentAnswer(),
                ctx.getCorrectAnswer());
    }

    /**
     * Xây dựng prompt để yêu cầu ChatGPT sinh recommendations.
     */
    private String buildRecommendationPrompt(Long studentId) {
        return String.format("""
                Bạn là một cố vấn học tập AI trong hệ thống Intelligent Tutoring System.
                Hãy đề xuất 2-3 bước học tập tiếp theo cho học sinh (Student ID: %d).

                Trả về JSON array với format:
                [
                    {
                        "nextTopic": "Tên chủ đề tiếp theo",
                        "explanation": "Giải thích tại sao nên học chủ đề này (2-3 câu)"
                    }
                ]

                Lưu ý:
                - Đề xuất thực tế và có thể thực hiện được
                - Giải thích ngắn gọn nhưng có ý nghĩa
                - Sử dụng tiếng Việt
                """, studentId);
    }

    /**
     * Gọi OpenAI ChatGPT API.
     */
    private String callChatGPT(String prompt) throws Exception {
        // Tạo request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // Tạo request body
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content",
                                "Bạn là một AI tutor thân thiện, chuyên hỗ trợ học sinh học tập."),
                        Map.of("role", "user", "content", prompt)),
                "temperature", 0.7,
                "max_tokens", 500);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        // Gọi API
        logger.info("Calling ChatGPT API with model: {}", model);
        ResponseEntity<String> response = restTemplate.exchange(
                OPENAI_API_URL,
                HttpMethod.POST,
                request,
                String.class);

        // Parse response
        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        String content = jsonResponse
                .path("choices")
                .path(0)
                .path("message")
                .path("content")
                .asText();

        logger.info("ChatGPT response received successfully");
        return content;
    }

    /**
     * Parse ChatGPT response thành AIFeedback object.
     */
    private AIFeedback parseFeedbackResponse(String response, SubmissionContext ctx) {
        try {
            // Tìm và parse JSON từ response
            String jsonContent = extractJson(response);
            JsonNode json = objectMapper.readTree(jsonContent);

            return AIFeedback.builder()
                    .studentId(ctx.getStudentId())
                    .questionId(ctx.getQuestionId())
                    .feedbackText(json.path("feedbackText").asText("Không có feedback"))
                    .hint(json.path("hint").asText("Không có gợi ý"))
                    .build();

        } catch (Exception e) {
            logger.warn("Failed to parse ChatGPT response as JSON. Using raw response.");
            // Nếu không parse được JSON, dùng raw response
            return AIFeedback.builder()
                    .studentId(ctx.getStudentId())
                    .questionId(ctx.getQuestionId())
                    .feedbackText(response)
                    .hint("Hãy xem lại bài làm của bạn và thử lại!")
                    .build();
        }
    }

    /**
     * Parse ChatGPT response thành list LearningRecommendation.
     */
    private List<LearningRecommendation> parseRecommendationResponse(String response, Long studentId) {
        List<LearningRecommendation> recommendations = new ArrayList<>();

        try {
            String jsonContent = extractJson(response);
            JsonNode jsonArray = objectMapper.readTree(jsonContent);

            if (jsonArray.isArray()) {
                for (JsonNode node : jsonArray) {
                    recommendations.add(LearningRecommendation.builder()
                            .studentId(studentId)
                            .nextTopic(node.path("nextTopic").asText("Chủ đề tiếp theo"))
                            .explanation(node.path("explanation").asText("Tiếp tục học tập!"))
                            .build());
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to parse recommendations. Using default.");
            recommendations.add(LearningRecommendation.builder()
                    .studentId(studentId)
                    .nextTopic("Ôn tập kiến thức")
                    .explanation(response)
                    .build());
        }

        return recommendations;
    }

    /**
     * Extract JSON từ response text (có thể có text thừa bao quanh).
     */
    private String extractJson(String text) {
        // Tìm vị trí bắt đầu và kết thúc của JSON
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');

        // Thử tìm JSON array
        if (start == -1) {
            start = text.indexOf('[');
            end = text.lastIndexOf(']');
        }

        if (start != -1 && end != -1 && end > start) {
            return text.substring(start, end + 1);
        }

        return text;
    }

    // ========== Personalized Learning (1.3.5) ==========

    /**
     * Chat trực tiếp với AI Learning Bot.
     * AI đọc lịch sử học tập để cá nhân hóa phản hồi.
     */
    @Override
    public String chat(Long studentId, String message, String context, List<AIFeedback> studentHistory) {
        if (apiKey == null || apiKey.isEmpty()) {
            logger.warn("OpenAI API key not configured. Using fallback.");
            return fallbackEngine.chat(studentId, message, context, studentHistory);
        }

        try {
            String prompt = buildChatPrompt(studentId, message, context, studentHistory);
            return callChatGPT(prompt);
        } catch (Exception e) {
            logger.error("Error in chat: {}. Using fallback.", e.getMessage());
            return "Xin lỗi, có lỗi xảy ra. Vui lòng thử lại sau!";
        }
    }

    /**
     * Gợi ý tài liệu học tập phù hợp.
     */
    @Override
    public String suggestMaterials(Long studentId, String currentTopic, List<String> weakTopics) {
        if (apiKey == null || apiKey.isEmpty()) {
            return fallbackEngine.suggestMaterials(studentId, currentTopic, weakTopics);
        }

        try {
            String prompt = buildMaterialsPrompt(studentId, currentTopic, weakTopics);
            return callChatGPT(prompt);
        } catch (Exception e) {
            logger.error("Error suggesting materials: {}", e.getMessage());
            return fallbackEngine.suggestMaterials(studentId, currentTopic, weakTopics);
        }
    }

    /**
     * Build prompt cho chat với context lịch sử học tập.
     */
    private String buildChatPrompt(Long studentId, String message, String context, List<AIFeedback> history) {
        StringBuilder historyStr = new StringBuilder();
        if (history != null && !history.isEmpty()) {
            historyStr.append("\nLịch sử học tập gần đây của học sinh:\n");
            for (AIFeedback fb : history) {
                historyStr.append(String.format("- Chủ đề: %s, Độ khó: %s, Kết quả: %s\n",
                        fb.getTopic() != null ? fb.getTopic() : "N/A",
                        fb.getDifficulty() != null ? fb.getDifficulty() : "N/A",
                        fb.getIsCorrect() != null && fb.getIsCorrect() ? "Đúng" : "Sai"));
            }
        }

        return String.format("""
                Bạn là AI Learning Bot - một trợ lý học tập thân thiện trong hệ thống ITS.

                Nhiệm vụ:
                - Trả lời câu hỏi của học sinh một cách dễ hiểu
                - Cá nhân hóa câu trả lời dựa trên lịch sử học tập
                - Khuyến khích và động viên học sinh
                - Gợi ý thêm tài liệu nếu phù hợp

                Student ID: %d
                %s
                %s

                Câu hỏi của học sinh: %s

                Hãy trả lời bằng tiếng Việt, thân thiện và dễ hiểu.
                Sử dụng emoji phù hợp để tăng tính tương tác.
                """,
                studentId,
                historyStr.toString(),
                context != null ? "Context học liệu: " + context : "",
                message);
    }

    /**
     * Build prompt để gợi ý tài liệu.
     */
    private String buildMaterialsPrompt(Long studentId, String currentTopic, List<String> weakTopics) {
        String weakStr = weakTopics != null ? String.join(", ", weakTopics) : "Chưa xác định";

        return String.format("""
                Bạn là cố vấn học tập AI. Hãy gợi ý tài liệu học tập phù hợp.

                Student ID: %d
                Chủ đề đang học: %s
                Các chủ đề cần cải thiện: %s

                Yêu cầu:
                1. Gợi ý 3-5 tài liệu/nguồn học tập cụ thể
                2. Ưu tiên tài liệu phù hợp với năng lực hiện tại
                3. Bao gồm cả lý thuyết và bài tập thực hành
                4. Giải thích ngắn gọn tại sao nên học mỗi tài liệu

                Trả lời bằng tiếng Việt, format dễ đọc với emoji.
                """,
                studentId, currentTopic, weakStr);
    }
}
