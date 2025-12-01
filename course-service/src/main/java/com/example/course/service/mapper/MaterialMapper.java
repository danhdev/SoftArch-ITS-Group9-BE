package com.example.course.service.mapper;

import com.example.course.service.dto.MaterialRequest;
import com.example.course.service.dto.MaterialResponse;
import com.example.course.service.model.Material;

public class MaterialMapper {

    public static Material toEntity(String chapterId, MaterialRequest data) {
        Material m = new Material();
        m.setChapterId(chapterId);
        m.setTitle(data.getTitle());
        m.setType(data.getType());
        m.setUrl(data.getUrl());
        return m;
    }

    public static MaterialResponse toResponse(Material m) {
        MaterialResponse res = new MaterialResponse();
        res.setId(m.getId());
        res.setTitle(m.getTitle());
        res.setType(m.getType());
        res.setUrl(m.getUrl());
        return res;
    }
}
