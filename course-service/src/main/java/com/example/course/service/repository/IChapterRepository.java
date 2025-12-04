package com.example.course.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.course.service.model.chapter.Chapter;

@Repository
public interface IChapterRepository extends JpaRepository<Chapter, String> {
    List<Chapter> findByCourseId(String courseId);
}
