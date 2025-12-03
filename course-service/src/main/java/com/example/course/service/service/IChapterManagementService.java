package com.example.course.service.service;

import java.util.List;

import com.example.course.service.dto.ChapterDTO;

public interface IChapterManagementService {
    void createChapter(String courseId, ChapterDTO chapter);
    void updateChapter(String courseId, ChapterDTO chapter);
    List<ChapterDTO> listChapters(String courseId);
    void deleteChapter(String chapterId);
}
