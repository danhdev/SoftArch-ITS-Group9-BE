package com.example.course.service.model.material;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("TEXT")
@Getter
@Setter
public class TextMaterial extends Material {
    @Column(columnDefinition = "TEXT")
    private String contentBody;
}
