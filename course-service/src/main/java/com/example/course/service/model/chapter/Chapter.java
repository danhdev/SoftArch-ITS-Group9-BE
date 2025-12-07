package com.example.course.service.model.chapter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.course.service.model.material.Material;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
@Component
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String courseId;
    String title;
    int orderIndex;
    DifficultyLevel difficulty;
    @OneToMany(mappedBy = "chapter")
    List<Material> materials;
}
