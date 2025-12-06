package com.example.demo.services.profiles;

import java.util.Optional;

import com.example.demo.dto.FeedbackInsights;
import com.example.demo.models.StudentProfile;

/**
 * Service interface for student profile operations.
 * Follows Interface Segregation Principle (ISP) - provides only profile-specific methods.
 */
public interface IStudentProfileService {

    /**
     * Get student profile by student ID.
     *
     * @param studentId the student's unique identifier
     * @return optional student profile
     */
    Optional<StudentProfile> getProfile(String studentId);

    /**
     * Update student profile based on feedback insights.
     *
     * @param studentId the student's unique identifier
     * @return the updated student profile
     */
//    StudentProfile updateProfileFromFeedback(String studentId, FeedbackInsights insights);
    StudentProfile updateProfileFromFeedback(String studentId);

    /**
     * Save or update a student profile.
     *
     * @param profile the student profile to save
     * @return the saved student profile
     */
    StudentProfile save(StudentProfile profile);
}
