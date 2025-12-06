package com.example.its.aifeedback.controller;

import com.example.its.aifeedback.dto.AIExplainRequestDTO;
import com.example.its.aifeedback.dto.AIExplainResponseDTO;
import com.example.its.aifeedback.dto.ResponseObject;
import com.example.its.aifeedback.service.AIExplainService;
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
@RequestMapping("/api/ai-explain")
@Tag(name = "AI Explain", description = "API cho AI Explain - Hỗ trợ giải thích tài liệu cá nhân hóa")
public class AIExplainController {

    private final AIExplainService aiExplainService;

    public AIExplainController(AIExplainService aiExplainService) {
        this.aiExplainService = aiExplainService;
    }

    @Operation(
            summary = "Tạo giải thích cho câu hỏi về tài liệu",
            description = "Tạo giải thích cá nhân hóa dựa trên câu hỏi của học sinh về nội dung tài liệu. " +
                    "AI sẽ phân tích nội dung tài liệu, lịch sử câu hỏi trước đó và đưa ra giải thích phù hợp."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo giải thích thành công",
                    content = @Content(schema = @Schema(implementation = ResponseObject.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ",
                    content = @Content(schema = @Schema(implementation = ResponseObject.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Thông tin câu hỏi của học sinh về tài liệu",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = AIExplainRequestDTO.class),
                    examples = @ExampleObject(
                            name = "Sample Request",
                            value = """
                                    {
                                        "studentId": 1,
                                        "materialId": 5,
                                        "studentQuestion": "Làm thế nào để tính đạo hàm của hàm số bậc hai?"
                                    }
                                    """
                    )
            )
    )
    @PostMapping("/generate")
    public ResponseEntity<ResponseObject<AIExplainResponseDTO>> generateExplanation(
            @Valid @RequestBody AIExplainRequestDTO request) {

        AIExplainResponseDTO explanation = aiExplainService.generateExplanation(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseObject.success("Tạo giải thích thành công", explanation));
    }

    @Operation(
            summary = "Lấy lịch sử giải thích",
            description = "Lấy tất cả các giải thích đã được tạo cho một học sinh và tài liệu cụ thể. " +
                    "Các giải thích được sắp xếp theo thứ tự thời gian tạo từ cũ đến mới."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy lịch sử giải thích thành công"),
            @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    @GetMapping("/history")
    public ResponseEntity<ResponseObject<List<AIExplainResponseDTO>>> getExplainHistory(
            @Parameter(description = "ID của học sinh", example = "1", required = true)
            @RequestParam Long studentId,
            @Parameter(description = "ID của tài liệu", example = "5", required = true)
            @RequestParam Long materialId) {

        List<AIExplainResponseDTO> history = aiExplainService.getExplainHistory(studentId, materialId);

        return ResponseEntity.ok(
                ResponseObject.success("Lấy lịch sử giải thích thành công", history));
    }
}
