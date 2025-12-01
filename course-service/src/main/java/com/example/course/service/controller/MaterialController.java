package com.example.course.service.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/materials")
public class MaterialController {

    @GetMapping("/ping")
    public String ping() {
        return "Material service OK!";
    }
}
