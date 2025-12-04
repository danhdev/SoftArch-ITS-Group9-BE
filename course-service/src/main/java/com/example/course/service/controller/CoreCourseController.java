package com.example.course.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;

import com.example.course.service.dto.ChapterDTO;
import com.example.course.service.dto.response.ResponseObject;
import com.example.course.service.service.IChapterManagementService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class CoreCourseController {
    @Autowired 
    @Qualifier("chapterManagementServiceImpl") 
    IChapterManagementService chapterManagementService;
    
    
    
    @PostMapping("chapter/{courseId}")
    public ResponseObject createChapter(@PathVariable String courseId, @RequestBody ChapterDTO chapterDTO) {
        //TODO: process POST request
        chapterManagementService.createChapter(courseId, chapterDTO);
        return new ResponseObject("created new chapter successfully", 201, chapterDTO);
    }

    @PutMapping("chapter/{courseId}")
    public ResponseObject updateChapter(@PathVariable String courseId, @RequestBody ChapterDTO chapterDTO) {
        //TODO: process POST request
        chapterManagementService.updateChapter(courseId, chapterDTO);
        return new ResponseObject("updated chapter successfully", 201, chapterDTO);
    }

    @GetMapping("chapters/{courseId}")
    public ResponseObject getChapterList() {
        return new ResponseObject("object responded", 200, chapterManagementService.listChapters(null));
    }
    
    @DeleteMapping("chapter/{id}") 
    public ResponseObject deleteChapter(@PathVariable String id) {
        return new ResponseObject("chapter deleted", 204, chapterManagementService.deleteChapter(id));
    }
}
