package com.example.its.aifeedback.controller;

import com.example.its.aifeedback.dto.ChatRequestDTO;
import com.example.its.aifeedback.dto.ChatResponseDTO;
import com.example.its.aifeedback.dto.ResponseObject;
import com.example.its.aifeedback.dto.StudentProfileDTO;
import com.example.its.aifeedback.service.AILearningBotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ================================
 * AI LEARNING BOT CONTROLLER
 * ================================
 * 
 * REST Controller cho AI Learning Bot - Personalized Learning (1.3.5).
 * 
 * Chức năng:
 * - POST /chat: Chat trực tiếp với AI Learning Bot
 * - GET /profile/{studentId}: Xem hồ sơ học tập
 * - GET /materials/{studentId}: Gợi ý tài liệu học tập
 * 
 * SOLID Principles Applied:
 * - SRP: Chỉ xử lý HTTP request/response cho Learning Bot
 * - DIP: Phụ thuộc vào AILearningBotService interface
 */
@RestController
@RequestMapping("/api/ai-learning-bot")
@Tag(name = "AI Learning Bot", description = "API cho AI Learning Bot - Hỗ trợ học tập cá nhân hóa")
public class AILearningBotController {

    private final AILearningBotService learningBotService;

    public AILearningBotController(AILearningBotService learningBotService) {
        this.learningBotService = learningBotService;
    }

    /**
     * Chat trực tiếp với AI Learning Bot.
     * 
     * Personalized Learning (1.3.5):
     * - Người học trao đổi trực tiếp với AI Learning Bot để được hỗ trợ
     * - AI đọc tài liệu liên quan và tạo giải thích theo hồ sơ học viên
     */
    @Operation(summary = "Chat với AI Learning Bot", description = "Trao đổi trực tiếp với AI Learning Bot để được hỗ trợ học tập. "
            +
            "AI sẽ cá nhân hóa câu trả lời dựa trên lịch sử học tập của bạn.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chat thành công"),
            @ApiResponse(responseCode = "400", description = "Request không hợp lệ"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Tin nhắn gửi đến AI Learning Bot", required = true, content = @Content(schema = @Schema(implementation = ChatRequestDTO.class), examples = @ExampleObject(name = "Sample Chat Request", value = """
            {
                "studentId": 1,
                "message": "Giải thích cho em về phương trình bậc 2 được không?",
                "currentTopic": "Đại số",
                "subject": "Toán học"
            }
            """)))
    @PostMapping("/chat")
    public ResponseEntity<ResponseObject<ChatResponseDTO>> chat(
            @Valid @RequestBody ChatRequestDTO request) {

        ChatResponseDTO response = learningBotService.chat(request);

        return ResponseEntity.ok(
                ResponseObject.success("Chat thành công", response));
    }

    /**
     * Xem hồ sơ học tập của học sinh.
     * 
     * Personalized Learning (1.3.5):
     * - Phân tích điểm mạnh, điểm yếu
     * - Gợi ý độ khó phù hợp
     * - Đề xuất chủ đề cần học tiếp
     */
    @Operation(summary = "Xem hồ sơ học tập", description = "Lấy phân tích chi tiết về quá trình học tập của học sinh. "
            +
            "Bao gồm: tỷ lệ đúng, điểm mạnh, điểm yếu, và đề xuất cải thiện.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy profile thành công"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/profile/{studentId}")
    public ResponseEntity<ResponseObject<StudentProfileDTO>> getStudentProfile(
            @Parameter(description = "ID của học sinh", example = "1") @PathVariable Long studentId) {

        StudentProfileDTO profile = learningBotService.getStudentProfile(studentId);

        return ResponseEntity.ok(
                ResponseObject.success("Lấy hồ sơ học tập thành công", profile));
    }

    /**
     * Gợi ý tài liệu học tập.
     * 
     * Personalized Learning (1.3.5):
     * - Gợi ý tài liệu phù hợp với năng lực và chủ đề
     * - Ưu tiên củng cố điểm yếu
     */
    @Operation(summary = "Gợi ý tài liệu học tập", description = "AI gợi ý tài liệu học tập phù hợp với năng lực và chủ đề của học sinh. "
            +
            "Tài liệu được cá nhân hóa dựa trên điểm mạnh/yếu.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gợi ý tài liệu thành công"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/materials/{studentId}")
    public ResponseEntity<ResponseObject<String>> suggestMaterials(
            @Parameter(description = "ID của học sinh", example = "1") @PathVariable Long studentId,
            @Parameter(description = "Chủ đề muốn học", example = "Phương trình bậc 2") @RequestParam(required = false, defaultValue = "Tổng hợp") String topic) {

        String materials = learningBotService.suggestMaterials(studentId, topic);

        return ResponseEntity.ok(
                ResponseObject.success("Gợi ý tài liệu thành công", materials));
    }
}
