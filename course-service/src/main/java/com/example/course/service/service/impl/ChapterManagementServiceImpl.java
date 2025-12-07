package com.example.course.service.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.course.service.dto.ChapterDTO;
import com.example.course.service.model.chapter.Chapter;
import com.example.course.service.repository.IChapterRepository;
import com.example.course.service.service.IChapterManagementService;

@Service
@Qualifier("chapterManagementServiceImpl")
public class ChapterManagementServiceImpl implements IChapterManagementService {
    @Autowired IChapterRepository chapterRepo;

    @Override
    public ChapterDTO createChapter(String courseId, ChapterDTO chapter) {
        // TODO Auto-generated method stub
        Chapter newChapter = mapToEntity(chapter);
        chapterRepo.save(newChapter);
        return chapter;
    }

    @Override
    public ChapterDTO updateChapter(String chapterId, ChapterDTO chapter) {
        // TODO Auto-generated method stub
        createChapter(chapterId, chapter);
        return chapter;
    }

    @Override
    public List<ChapterDTO> listChapters(String courseId) {
        // TODO Auto-generated method stub
        List<Chapter> chapters = chapterRepo.findByCourseId(courseId);
        return chapters.stream().map(chap -> mapToDTO(chap)).toList();
    }

    @Override
    public ChapterDTO deleteChapter(String chapterId) {
        // TODO Auto-generated method stub
        Chapter chapterDeleted = chapterRepo.findById(chapterId).orElseThrow();
        chapterRepo.delete(chapterDeleted);
        return mapToDTO(chapterDeleted);
    }

    public ChapterDTO mapToDTO(Chapter chapter) {
        ChapterDTO chapterDTO = new ChapterDTO(chapter.getCourseId(), chapter.getTitle(), chapter.getOrderIndex(), chapter.getDifficulty());
        return chapterDTO;
    }

    public Chapter mapToEntity(ChapterDTO chapterDTO) {
        Chapter chapter = new Chapter();
        chapter.setCourseId(chapterDTO.getCourseId());
        chapter.setTitle(chapterDTO.getTitle());
        chapter.setOrderIndex(chapterDTO.getOrderIndex());
        chapter.setDifficulty(chapterDTO.getDifficulty());
        return chapter;
    }

}
