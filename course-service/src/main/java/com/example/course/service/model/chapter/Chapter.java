package com.example.course.service.model.chapter;

import java.util.List;

import com.example.course.service.model.material.Material;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;

@Entity
@Data
public class Chapter {
    @Id String id;
    String courseId;
    String title;
    int orderIndex;
    DifficultyLevel difficulty;
    @ManyToMany
    List<Material> materials;
}
