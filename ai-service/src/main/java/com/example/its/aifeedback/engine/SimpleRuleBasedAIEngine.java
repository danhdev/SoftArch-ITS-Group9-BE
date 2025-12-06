package com.example.its.aifeedback.engine;

import com.example.its.aifeedback.domain.AIFeedback;
import com.example.its.aifeedback.domain.ExplainSubmissionContext;
import com.example.its.aifeedback.domain.HintSubmissionContext;
import com.example.its.aifeedback.domain.LearningRecommendation;
import com.example.its.aifeedback.domain.SubmissionContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================
 * STRATEGY PATTERN - Concrete Implementation
 * ================================
 * 
 * Simple rule-based implementation of AIEngine.
 * This is the default strategy that uses basic string matching and rules.
 * 
 * SOLID Principles Applied:
 * - SRP: This class has one responsibility - generating feedback using rules
 * - LSP: Can be substituted for any AIEngine implementation
 * - OCP: Adding new rules doesn't require modifying the interface
 * 
 * In a production system, this could be replaced with:
 * - OpenAI GPT integration
 * - Google Gemini integration
 * - Custom ML model integration
 * 
 * The @Component annotation ensures Spring can inject this as the default
 * AIEngine.
 * To switch strategies, you can:
 * 1. Use @Primary on preferred implementation
 * 2. Use @Qualifier to specify which implementation to inject
 * 3. Use configuration to select implementation at runtime
 */
@Component
public class SimpleRuleBasedAIEngine implements AIEngine {

    /**
     * Generates feedback based on simple rule matching.
     * Compares student answer with correct answer (case-insensitive, trimmed).
     */
    @Override
    public AIFeedback generateFeedback(SubmissionContext ctx) {
        String studentAnswer = normalize(ctx.getStudentAnswer());
        String correctAnswer = normalize(ctx.getCorrectAnswer());

        String feedbackText;
        String hint;

        if (studentAnswer.equals(correctAnswer)) {
            // Correct answer - provide positive reinforcement
            feedbackText = generateCorrectFeedback(ctx);
            hint = generateMasteryHint(ctx);
        } else if (isPartiallyCorrect(studentAnswer, correctAnswer)) {
            // Partially correct - encourage and guide
            feedbackText = generatePartialFeedback(ctx);
            hint = generateGuidingHint(ctx);
        } else {
            // Incorrect - provide constructive feedback
            feedbackText = generateIncorrectFeedback(ctx);
            hint = generateHelpfulHint(ctx);
        }

        return AIFeedback.builder()
                .studentId(ctx.getStudentId())
                .questionId(ctx.getQuestionId())
                .feedbackText(feedbackText)
                .hint(hint)
                .build();
    }

    /**
     * Suggests next learning steps based on current topic and difficulty.
     * In a real system, this would analyze the student's history and performance.
     */
    @Override
    public List<LearningRecommendation> suggestNextSteps(Long studentId) {
        List<LearningRecommendation> recommendations = new ArrayList<>();

        // Recommendation 1: Review fundamentals
        recommendations.add(LearningRecommendation.builder()
                .studentId(studentId)
                .nextTopic("Review Fundamentals")
                .explanation("Strengthening your foundation will help you tackle more advanced problems. " +
                        "Consider revisiting the basic concepts before moving forward.")
                .build());

        // Recommendation 2: Practice exercises
        recommendations.add(LearningRecommendation.builder()
                .studentId(studentId)
                .nextTopic("Practice Exercises")
                .explanation("Apply what you've learned with hands-on practice. " +
                        "Try solving similar problems to reinforce your understanding.")
                .build());

        return recommendations;
    }

    @Override
    public String generateHint(HintSubmissionContext ctx) {
        int previousHintCount = ctx.getPreviousHints() != null ? ctx.getPreviousHints().size() : 0;

        if (previousHintCount == 0) {
            return generateBasicHint(ctx);
        } else if (previousHintCount == 1) {
            return generateIntermediateHint(ctx);
        } else {
            return generateDetailedHint(ctx);
        }
    }

    @Override
    public String generateExplanation(ExplainSubmissionContext ctx) {
        StringBuilder explanation = new StringBuilder();

        explanation.append("📖 Giải thích:\n\n");

        if (ctx.getMaterialContent() != null && !ctx.getMaterialContent().isEmpty()) {
            explanation.append("Dựa trên tài liệu học tập, đây là giải thích cho câu hỏi của bạn:\n\n");

            if (ctx.getStudentQuestion() != null && !ctx.getStudentQuestion().isEmpty()) {
                explanation.append("❓ Câu hỏi: ").append(ctx.getStudentQuestion()).append("\n\n");
            }

            explanation.append("💡 ");

            int previousCount = ctx.getPreviousQuestions() != null ? ctx.getPreviousQuestions().size() : 0;
            if (previousCount == 0) {
                explanation.append("Hãy xem lại nội dung tài liệu một cách cẩn thận. ");
                explanation.append("Tìm các từ khóa liên quan đến câu hỏi của bạn trong tài liệu. ");
                explanation.append("Thử kết nối các khái niệm với nhau để hiểu rõ hơn.\n\n");
            } else if (previousCount == 1) {
                explanation.append("Để hiểu rõ hơn, hãy suy nghĩ về câu hỏi từ góc độ khác. ");
                explanation.append("So sánh các ví dụ trong tài liệu với tình huống cụ thể bạn đang thắc mắc. ");
                explanation.append("Đôi khi việc vẽ sơ đồ hoặc viết ra các bước có thể giúp làm rõ khái niệm.\n\n");
            } else {
                explanation.append("Hãy thử phân tích chi tiết từng phần của câu hỏi. ");
                explanation.append("Xác định các khái niệm chính, sau đó tìm định nghĩa và ví dụ trong tài liệu. ");
                explanation.append("Nếu vẫn chưa rõ, hãy thử giải thích lại bằng lời của bạn để kiểm tra sự hiểu biết.\n\n");
            }

            explanation.append("📚 Gợi ý: Đọc kỹ phần liên quan trong tài liệu, ");
            explanation.append("tìm các ví dụ minh họa, và thử áp dụng vào trường hợp cụ thể. ");
            explanation.append("Đừng ngại đặt thêm câu hỏi nếu vẫn chưa hiểu rõ!");

        } else {
            explanation.append("⚠️ Hiện tại chưa có nội dung tài liệu để tham khảo. ");
            explanation.append("Tuy nhiên, đối với câu hỏi: \"");
            explanation.append(ctx.getStudentQuestion() != null ? ctx.getStudentQuestion() : "câu hỏi của bạn");
            explanation.append("\", hãy thử:\n\n");
            explanation.append("1. Xác định các từ khóa chính trong câu hỏi\n");
            explanation.append("2. Nghĩ về kiến thức nền tảng liên quan\n");
            explanation.append("3. Kết nối các khái niệm với nhau\n");
            explanation.append("4. Tìm kiếm thêm tài liệu tham khảo nếu cần\n\n");
            explanation.append("💪 Hãy tiếp tục học tập và đặt câu hỏi!");
        }

        return explanation.toString();
    }

    // ========== Private Helper Methods ==========

    private String normalize(String text) {
        return text == null ? "" : text.trim().toLowerCase();
    }

    private boolean isPartiallyCorrect(String studentAnswer, String correctAnswer) {
        // Check if student answer contains key parts of correct answer
        if (studentAnswer.isEmpty() || correctAnswer.isEmpty()) {
            return false;
        }

        // Simple partial match: check if answers share significant content
        String[] correctWords = correctAnswer.split("\\s+");
        int matchCount = 0;

        for (String word : correctWords) {
            if (word.length() > 2 && studentAnswer.contains(word)) {
                matchCount++;
            }
        }

        return matchCount > 0 && matchCount < correctWords.length;
    }

    private String generateCorrectFeedback(SubmissionContext ctx) {
        String topic = ctx.getTopic() != null ? ctx.getTopic() : "this topic";
        String difficulty = ctx.getDifficulty() != null ? ctx.getDifficulty() : "standard";

        return String.format(
                "🎉 Excellent work! Your answer is correct. " +
                        "You've demonstrated a solid understanding of %s at the %s level. " +
                        "Keep up the great work!",
                topic, difficulty);
    }

    private String generatePartialFeedback(SubmissionContext ctx) {
        String topic = ctx.getTopic() != null ? ctx.getTopic() : "this concept";

        return String.format(
                "👍 You're on the right track! Your answer shows partial understanding of %s. " +
                        "Review your response and think about what might be missing or needs clarification.",
                topic);
    }

    private String generateIncorrectFeedback(SubmissionContext ctx) {
        String topic = ctx.getTopic() != null ? ctx.getTopic() : "this topic";

        return String.format(
                "📚 Not quite right, but don't worry - mistakes are part of learning! " +
                        "Let's review %s together. Take a moment to reconsider the question and try again.",
                topic);
    }

    private String generateMasteryHint(SubmissionContext ctx) {
        String difficulty = ctx.getDifficulty();

        if ("easy".equalsIgnoreCase(difficulty)) {
            return "💡 Ready for a challenge? Try some medium difficulty questions to test your skills further!";
        } else if ("medium".equalsIgnoreCase(difficulty)) {
            return "💡 Great progress! Consider attempting hard questions to push your boundaries.";
        } else {
            return "💡 Impressive! You've mastered this level. Explore advanced topics or help others learn!";
        }
    }

    private String generateGuidingHint(SubmissionContext ctx) {
        return String.format(
                "💡 Hint: Look at the question again. The key concept involves '%s'. " +
                        "Think about how each part of your answer relates to the question.",
                ctx.getTopic() != null ? ctx.getTopic() : "the main subject");
    }

    private String generateHelpfulHint(SubmissionContext ctx) {
        String questionText = ctx.getQuestionText();

        // Extract a hint from the question without revealing the answer
        if (questionText != null && questionText.length() > 20) {
            return String.format(
                    "💡 Hint: Re-read the question carefully. Pay attention to key terms. " +
                            "The answer relates to %s. Consider what you know about this topic.",
                    ctx.getTopic() != null ? ctx.getTopic() : "the concept being tested");
        }

        return "💡 Hint: Break down the problem into smaller parts. " +
                "What do you know for sure? Start from there and build your answer step by step.";
    }

    private String generateBasicHint(HintSubmissionContext ctx) {
        return String.format(
                "💡 Gợi ý đầu tiên: Hãy đọc kỹ câu hỏi và suy nghĩ về chủ đề '%s'. " +
                "Bạn đã học những gì về chủ đề này?",
                ctx.getTopic() != null ? ctx.getTopic() : "này");
    }

    private String generateIntermediateHint(HintSubmissionContext ctx) {
        return String.format(
                "💡 Gợi ý thứ hai: Hãy phân tích câu hỏi thành các phần nhỏ hơn. " +
                "Với chủ đề '%s', hãy nghĩ về phương pháp hoặc công thức có thể áp dụng.",
                ctx.getTopic() != null ? ctx.getTopic() : "này");
    }

    private String generateDetailedHint(HintSubmissionContext ctx) {
        return String.format(
                "💡 Gợi ý chi tiết: Hãy thử từng bước một. " +
                "Bước 1: Xác định những gì đề bài cho. " +
                "Bước 2: Xác định những gì cần tìm. " +
                "Bước 3: Áp dụng kiến thức về '%s' để kết nối hai điều trên. " +
                "Nếu vẫn gặp khó khăn, hãy xem lại tài liệu học tập!",
                ctx.getTopic() != null ? ctx.getTopic() : "chủ đề này");
    }
}
