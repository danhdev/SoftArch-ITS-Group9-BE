package com.example.course.service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.course.service.dto.ChapterDTO;

@Service
public interface IChapterManagementService {
    ChapterDTO createChapter(String courseId, ChapterDTO chapter);
    ChapterDTO updateChapter(String courseId, ChapterDTO chapter);
    List<ChapterDTO> listChapters(String courseId);
    ChapterDTO deleteChapter(String chapterId);
}
