package com.example.course.service.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.course.service.dto.ChapterDTO;
import com.example.course.service.service.IChapterManagementService;

@Service
public class ChapterManagementServiceImpl implements IChapterManagementService {
    

    @Override
    public void createChapter(String courseId, ChapterDTO chapter) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createChapter'");
    }

    @Override
    public void updateChapter(String courseId, ChapterDTO chapter) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateChapter'");
    }

    @Override
    public List<ChapterDTO> listChapters(String courseId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listChapters'");
    }

    @Override
    public void deleteChapter(String chapterId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteChapter'");
    }

}
