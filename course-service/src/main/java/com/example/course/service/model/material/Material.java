package com.example.course.service.model.material;

import com.example.course.service.model.chapter.Chapter;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "materials")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // Lưu tất cả vào 1 bảng, phân loại bằng cột discriminator
@DiscriminatorColumn(name = "material_type")
@Getter
@Setter
public abstract class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String title;

    @Column(name = "material_type", insertable = false, updatable = false)
    private String type;
    @ManyToOne
    @JoinColumn(name = "chapterId")
    Chapter chapter;
}