package com.example.course.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import com.example.course.service.dto.MaterialDTO;
import com.example.course.service.dto.response.ResponseObject;
import com.example.course.service.service.IMaterialManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.example.course.service.dto.ChapterDTO;
import com.example.course.service.service.IChapterManagementService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("course/{courseId}")
@RequiredArgsConstructor
@Tag(name = "Core Course Controller", description = "Quản lý nội dung khóa học, chapter và material")
public class CoreCourseController {
    @Autowired 
    @Qualifier("chapterManagementServiceImpl") 
    IChapterManagementService chapterManagementService;
    
    
    @Operation(summary = "Tạo chapter mới cho course")
    @PostMapping("chapter")
    public ResponseEntity<ResponseObject> createChapter(@PathVariable String courseId, @RequestBody ChapterDTO chapterDTO) {
        //TODO: process POST request
        chapterManagementService.createChapter(courseId, chapterDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseObject.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Query materials successfully")
                        .data(chapterDTO)
                        .build()
        );
    }

    @Operation(summary = "Cập nhật chapter đã có trong course")
    @PutMapping("chapter")
    public ResponseEntity<ResponseObject> updateChapter(@PathVariable String courseId, @RequestBody ChapterDTO chapterDTO) {
        //TODO: process POST request
        chapterManagementService.updateChapter(courseId, chapterDTO);
        // return new ResponseObject("updated chapter successfully", 201, chapterDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseObject.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Query materials successfully")
                        .data(chapterDTO)
                        .build()
        );
    }

    @Operation(summary = "Lấy danh sách các chapter hiện có")
    @GetMapping("chapters")
    public ResponseEntity<ResponseObject> getChapterList(@PathVariable String courseId) {
        // return new ResponseObject("object responded", 200, chapterManagementService.listChapters(null));
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .status(HttpStatus.OK.value())
                        .message("Query materials successfully")
                        .data(chapterManagementService.listChapters(courseId))
                        .build()
        );
    }
    
    @Operation(summary = "Xóa chapter với id")
    @DeleteMapping("chapter/{id}") 
    public ResponseEntity<ResponseObject> deleteChapter(@PathVariable String id) {
        // return new ResponseObject("chapter deleted", 204, chapterManagementService.deleteChapter(id));
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .status(HttpStatus.OK.value())
                        .message("Query materials successfully")
                        .data(chapterManagementService.deleteChapter(id))
                        .build()
        );
    }
    private final IMaterialManagementService materialService;

    @Operation(summary = "Lấy danh sách Material của một Chapter")
    @GetMapping("/chapters/{chapterId}/materials")
    public ResponseEntity<ResponseObject> getMaterialsByChapter(@PathVariable String chapterId) {
        List<MaterialDTO> materials = materialService.listMaterials(chapterId);

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .status(HttpStatus.OK.value())
                        .message("Query materials successfully")
                        .data(materials)
                        .build()
        );
    }

    @Operation(summary = "Tạo Material mới trong Chapter")
    @PostMapping("/chapters/{chapterId}/materials")
    public ResponseEntity<ResponseObject> createMaterial(@PathVariable String chapterId, @RequestBody MaterialDTO materialDTO) {
        materialService.createMaterial(chapterId, materialDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseObject.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Insert material successfully")
                        .data(materialDTO)
                        .build()
        );
    }

    @Operation(summary = "Cập nhật Material")
    @PutMapping("/materials/{materialId}")
    public ResponseEntity<ResponseObject> updateMaterial(@PathVariable String materialId, @RequestBody MaterialDTO materialDTO) {
        materialService.updateMaterial(materialId, materialDTO);

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .status(HttpStatus.OK.value())
                        .message("Update material successfully")
                        .data(materialDTO)
                        .build()
        );
    }

    @Operation(summary = "Xóa Material")
    @DeleteMapping("/materials/{materialId}")
    public ResponseEntity<ResponseObject> deleteMaterial(@PathVariable String materialId) {
        materialService.deleteMaterial(materialId);

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .status(HttpStatus.NO_CONTENT.value())
                        .message("Delete material successfully")
                        .data("")
                        .build()
        );
    }
}
