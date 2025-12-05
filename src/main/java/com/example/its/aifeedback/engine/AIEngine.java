package com.example.its.aifeedback.engine;

import com.example.its.aifeedback.domain.*;

import java.util.List;

/**
 * ================================
 * STRATEGY PATTERN - AIEngine Interface
 * ================================
 * 
 * This interface defines the contract for AI feedback generation engines.
 * 
 * SOLID Principles Applied:
 * - OCP (Open/Closed): New AI engines can be added without modifying existing
 * code
 * - LSP (Liskov Substitution): Any implementation can be substituted without
 * breaking the system
 * - DIP (Dependency Inversion): Services depend on this abstraction, not
 * concrete implementations
 * - ISP (Interface Segregation): Focused interface with cohesive
 * responsibilities
 * 
 * Why Strategy Pattern?
 * - Allows swapping AI implementations at runtime (rule-based, ML-based,
 * LLM-based)
 * - Makes testing easier with mock implementations
 * - Enables gradual migration from simple rules to complex AI models
 * 
 * Personalized Learning (1.3.5):
 * - Chat với AI Learning Bot
 * - Tạo giải thích theo hồ sơ học viên
 * - Gợi ý tài liệu phù hợp
 * 
 * Implementations:
 * - SimpleRuleBasedAIEngine: Basic rule-based logic (fallback)
 * - ChatGPTAIEngine: OpenAI GPT integration (primary)
 * - Future: GeminiEngine, CustomMLEngine, etc.
 */
public interface AIEngine {

    /**
     * Generates intelligent feedback for a student's submission.
     * 
     * @param ctx the submission context containing question and answer details
     * @return AIFeedback with personalized feedback and hints
     */
    AIFeedback generateFeedback(SubmissionContext ctx);

    /**
     * Suggests next learning steps for a student based on their history.
     * 
     * @param studentId the ID of the student
     * @return list of learning recommendations
     */
    List<LearningRecommendation> suggestNextSteps(Long studentId);

    String generateHint(HintSubmissionContext ctx);

    String generateExplanation(ExplainSubmissionContext ctx);

    /**
     * Suggests next learning steps với dữ liệu phân tích chi tiết.
     * Đây là phiên bản enhanced để tạo recommendations thông minh hơn.
     * 
     * @param studentId      ID học sinh
     * @param totalAttempts  Tổng số bài đã làm
     * @param correctCount   Số câu trả lời đúng
     * @param strongTopics   Các chủ đề mạnh (topic, count)
     * @param weakTopics     Các chủ đề yếu (topic, count)
     * @param recentHistory  Lịch sử học tập gần đây
     * @return list of learning recommendations dựa trên phân tích
     */
    default List<LearningRecommendation> suggestNextSteps(
            Long studentId,
            long totalAttempts,
            long correctCount,
            List<Object[]> strongTopics,
            List<Object[]> weakTopics,
            List<AIFeedback> recentHistory) {
        // Default: delegate to simple version
        return suggestNextSteps(studentId);
    }

    /**
     * ================================
     * Personalized Learning (1.3.5)
     * ================================
     * 
     * Chat trực tiếp với AI Learning Bot.
     * AI sẽ đọc tài liệu liên quan và tạo giải thích theo hồ sơ học viên.
     * 
     * @param studentId      ID học sinh
     * @param message        Tin nhắn/câu hỏi của học sinh
     * @param context        Context học liệu (optional)
     * @param studentHistory Lịch sử học tập gần đây (để cá nhân hóa)
     * @return Phản hồi từ AI
     */
    default String chat(Long studentId, String message, String context, List<AIFeedback> studentHistory) {
        // Default implementation - có thể override trong subclass
        return "AI Learning Bot đang được phát triển. Vui lòng thử lại sau!";
    }

    /**
     * Gợi ý tài liệu học tập phù hợp với năng lực và chủ đề của người học.
     * 
     * @param studentId    ID học sinh
     * @param currentTopic Chủ đề đang học
     * @param weakTopics   Các chủ đề yếu (từ phân tích)
     * @return Danh sách tài liệu được gợi ý
     */
    default String suggestMaterials(Long studentId, String currentTopic, List<String> weakTopics) {
        // Default implementation
        StringBuilder sb = new StringBuilder();
        sb.append("📚 Tài liệu gợi ý:\n");
        sb.append("1. Ôn tập lý thuyết cơ bản về ").append(currentTopic).append("\n");
        sb.append("2. Bài tập thực hành\n");
        if (weakTopics != null && !weakTopics.isEmpty()) {
            sb.append("3. Củng cố kiến thức: ").append(String.join(", ", weakTopics));
        }
        return sb.toString();
    }
}
