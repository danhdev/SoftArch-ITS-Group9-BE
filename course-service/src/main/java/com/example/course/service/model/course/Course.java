package com.example.course.service.model.course;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "courses")
@Data 
@NoArgsConstructor 
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id; 

    @Column(name = "course_name", nullable = false)
    private String name;

    private String description;
    private String courseStatus; 

    @Column(name = "teacher_id")
    private String teacherId; 
}
