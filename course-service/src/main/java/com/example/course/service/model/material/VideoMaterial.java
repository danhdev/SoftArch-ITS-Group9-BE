package com.example.course.service.model.material;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("VIDEO")
@Setter
@Getter
public class VideoMaterial extends Material {
    private String videoUrl;
    private int duration;
    private String transcript;

    public void setVideoUrl(String contentOrUrl) {
    }
}