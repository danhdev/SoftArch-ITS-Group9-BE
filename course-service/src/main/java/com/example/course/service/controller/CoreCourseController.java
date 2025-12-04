package com.example.course.service.controller;

import com.example.course.service.dto.CourseDTO;
import com.example.course.service.dto.MaterialDTO;
import com.example.course.service.dto.response.ResponseObject;
import com.example.course.service.service.ICourseManagementService;
import com.example.course.service.service.IMaterialManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Core Course Controller", description = "Quản lý nội dung khóa học, chapter và material")
public class CoreCourseController {

    private final IMaterialManagementService materialService;
    private final ICourseManagementService courseService;

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

    @Operation(summary = "Tạo mới Course")
    @PostMapping("/courses")
    public ResponseEntity<ResponseObject> createCourse(@RequestBody CourseDTO courseDTO) {
        courseService.createCourse(courseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseObject.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Insert course successfully")
                        .data(courseDTO)
                        .build()
        );
    }

    @Operation(summary = "Cập nhật Course")
    @PutMapping("/courses/{courseId}")
    public ResponseEntity<ResponseObject> updateCourse(@PathVariable String courseId, @RequestBody CourseDTO courseDTO) {
        courseService.updateCourse(courseId, courseDTO);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .status(HttpStatus.OK.value())
                        .message("Update course successfully")
                        .data(courseDTO)
                        .build()
        );
    }

    @Operation(summary = "Tìm kiếm Course theo tên")
    @GetMapping("/courses/search")
    public ResponseEntity<ResponseObject> searchCourses(@RequestParam String keyword) {
        List<CourseDTO> courses = courseService.searchCourses(keyword);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .status(HttpStatus.OK.value())
                        .message("Search courses successfully")
                        .data(courses)
                        .build()
        );
    }

    @Operation(summary = "Lấy chi tiết Course")
    @GetMapping("/courses/{courseId}")
    public ResponseEntity<ResponseObject> getCourseDetail(@PathVariable String courseId) {
        List<CourseDTO> courseDetail = courseService.getCourseDetail(courseId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .status(HttpStatus.OK.value())
                        .message("Get course detail successfully")
                        .data(courseDetail)
                        .build()
        );
    }

    @Operation(summary = "Xóa Course")
    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<ResponseObject> deleteCourse(@PathVariable String courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                ResponseObject.builder()
                        .status(HttpStatus.NO_CONTENT.value())
                        .message("Delete course successfully")
                        .data("")
                        .build()
        );
    }
}
