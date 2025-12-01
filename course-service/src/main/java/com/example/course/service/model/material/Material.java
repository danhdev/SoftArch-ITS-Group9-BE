package com.example.course.service.model.material;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // Lưu tất cả vào 1 bảng, phân loại bằng cột discriminator
@DiscriminatorColumn(name = "material_type")
@Getter
@Setter
public abstract class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String chapterId;
    private String title;

    @Column(insertable = false, updatable = false)
    private String type;
}