package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.StudentProfile;

/**
 * Repository interface for StudentProfile entity.
 * Follows the Interface Segregation Principle (ISP) by extending JpaRepository
 * and adding only necessary custom query methods.
 */
@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, String> {

    /**
     * Find student profile by student ID.
     *
     * @param studentId the student's unique identifier
     * @return optional student profile
     */
    Optional<StudentProfile> findByStudentId(String studentId);
}
