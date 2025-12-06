package com.example.demo.services.profiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.StudentProfile;
import com.example.demo.repository.StudentProfileRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of StudentProfileService.
 * Follows Single Responsibility Principle - handles only student profile management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StudentProfileServiceImpl implements IStudentProfileService {

    private final StudentProfileRepository studentProfileRepository;

    @Override
    public Optional<StudentProfile> getProfile(String studentId) {
        log.debug("Fetching profile for student: {}", studentId);
        return studentProfileRepository.findByStudentId(studentId);
    }

    @Override
    @Transactional
    public StudentProfile updateProfileFromFeedback(String studentId) {
        log.info("Updating profile for student: {} based on feedback", studentId);

        StudentProfile profile = studentProfileRepository.findByStudentId(studentId)
                .orElse(StudentProfile.builder()
                        .studentId(studentId)
                        .skillMasteryMap(new HashMap<>())
                        .weaknessAreas(new ArrayList<>())
                        .recommendedLearningPath(new ArrayList<>())
                        .build());

        // TODO: Add logic to update profile based on feedback when FeedbackProcessor is enabled
        // For now, just ensure the profile exists and is saved

        return studentProfileRepository.save(profile);
    }

    @Override
    public StudentProfile save(StudentProfile profile) {
        log.debug("Saving profile for student: {}", profile.getStudentId());
        return studentProfileRepository.save(profile);
    }
}
