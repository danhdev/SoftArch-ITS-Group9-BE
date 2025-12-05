package com.example.its.aifeedback.controller;

import com.example.its.aifeedback.dto.HintRequestDTO;
import com.example.its.aifeedback.dto.HintResponseDTO;
import com.example.its.aifeedback.dto.ResponseObject;
import com.example.its.aifeedback.service.AIHintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai-hint")
@Tag(name = "AI Hint", description = "API cho AI Hint - Hỗ trợ tạo gợi ý cá nhân hóa")
public class AIHintController {

    private final AIHintService aiHintService;

    public AIHintController(AIHintService aiHintService) {
        this.aiHintService = aiHintService;
    }

    @Operation(
            summary = "Tạo gợi ý cho câu hỏi",
            description = "Tạo gợi ý cá nhân hóa dựa trên câu hỏi, câu trả lời đúng, và các gợi ý trước đó của học sinh. " +
                    "AI sẽ phân tích lịch sử gợi ý và tài liệu khóa học để đưa ra gợi ý phù hợp."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo gợi ý thành công",
                    content = @Content(schema = @Schema(implementation = ResponseObject.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ",
                    content = @Content(schema = @Schema(implementation = ResponseObject.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Thông tin câu hỏi và học sinh để tạo gợi ý",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = HintRequestDTO.class),
                    examples = @ExampleObject(
                            name = "Sample Request",
                            value = """
                                    {
                                        "studentId": 1,
                                        "questionId": 101,
                                        "subjectId": 5,
                                        "questionText": "Tính đạo hàm của hàm số f(x) = x^2 + 3x + 2",
                                        "correctAnswer": "f'(x) = 2x + 3",
                                        "subjectId": "MATH101",
                                        "subject": "Toán học",
                                        "topic": "Đạo hàm",
                                        "difficulty": "medium"
                                    }
                                    """
                    )
            )
    )
    @PostMapping("/generate")
    public ResponseEntity<ResponseObject<HintResponseDTO>> generateHint(
            @Valid @RequestBody HintRequestDTO request) {

        HintResponseDTO hint = aiHintService.generateHint(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseObject.success("Tạo gợi ý thành công", hint));
    }

    @Operation(
            summary = "Lấy lịch sử gợi ý",
            description = "Lấy tất cả các gợi ý đã được tạo cho một câu hỏi và học sinh cụ thể. " +
                    "Các gợi ý được sắp xếp theo thứ tự thời gian tạo."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy lịch sử gợi ý thành công"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/history")
    public ResponseEntity<ResponseObject<List<HintResponseDTO>>> getHintHistory(
            @Parameter(description = "ID của học sinh", example = "1", required = true)
            @RequestParam Long studentId,
            @Parameter(description = "ID của câu hỏi", example = "101", required = true)
            @RequestParam Long questionId) {

        List<HintResponseDTO> history = aiHintService.getHintHistory(studentId, questionId);

        return ResponseEntity.ok(
                ResponseObject.success("Lấy lịch sử gợi ý thành công", history));
    }
}
