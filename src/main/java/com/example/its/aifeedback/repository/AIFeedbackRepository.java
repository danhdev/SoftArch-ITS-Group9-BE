package com.example.its.aifeedback.repository;

import com.example.its.aifeedback.domain.AIFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for AIFeedback entity persistence.
 * 
 * SOLID Principles Applied:
 * - ISP: Interface only exposes methods needed by consumers
 * - DIP: Service layer depends on this interface, not implementation
 * 
 * Personalized Learning (1.3.5):
 * - Cung cấp methods để phân tích hồ sơ học viên
 * - Hỗ trợ gợi ý dựa trên lịch sử học tập
 * 
 * Spring Data JPA automatically provides implementation at runtime.
 * Custom query methods follow Spring Data naming conventions.
 */
@Repository
public interface AIFeedbackRepository extends JpaRepository<AIFeedback, Long> {

    /**
     * Find all feedback records for a specific student.
     * Used for retrieving feedback history.
     * 
     * @param studentId the student's ID
     * @return list of all feedback for the student
     */
    List<AIFeedback> findByStudentId(Long studentId);

    /**
     * Find the most recent feedback for a student.
     * Orders by ID descending (assuming auto-increment = chronological order)
     * and takes the first result.
     * 
     * @param studentId the student's ID
     * @return the latest feedback if exists
     */
    Optional<AIFeedback> findTopByStudentIdOrderByIdDesc(Long studentId);

    /**
     * Find all feedback for a specific question.
     * Useful for analytics and question quality assessment.
     * 
     * @param questionId the question's ID
     * @return list of all feedback for the question
     */
    List<AIFeedback> findByQuestionId(Long questionId);

    /**
     * Find feedback for a specific student-question combination.
     * 
     * @param studentId  the student's ID
     * @param questionId the question's ID
     * @return list of feedback (may have multiple attempts)
     */
    List<AIFeedback> findByStudentIdAndQuestionId(Long studentId, Long questionId);

    // ========== Methods cho Personalized Learning (1.3.5) ==========

    /**
     * Đếm tổng số bài làm của học sinh
     */
    long countByStudentId(Long studentId);

    /**
     * Đếm số câu trả lời đúng
     */
    long countByStudentIdAndIsCorrect(Long studentId, Boolean isCorrect);

    /**
     * Lấy feedback theo topic để phân tích điểm mạnh/yếu
     */
    List<AIFeedback> findByStudentIdAndTopic(Long studentId, String topic);

    /**
     * Lấy feedback theo độ khó
     */
    List<AIFeedback> findByStudentIdAndDifficulty(Long studentId, String difficulty);

    /**
     * Lấy các topic mà học sinh làm sai nhiều nhất
     * Query để phân tích điểm yếu
     */
    @Query("SELECT f.topic, COUNT(f) as cnt FROM AIFeedback f " +
            "WHERE f.studentId = :studentId AND f.isCorrect = false " +
            "GROUP BY f.topic ORDER BY cnt DESC")
    List<Object[]> findWeakTopicsByStudentId(@Param("studentId") Long studentId);

    /**
     * Lấy các topic mà học sinh làm đúng nhiều nhất
     * Query để phân tích điểm mạnh
     */
    @Query("SELECT f.topic, COUNT(f) as cnt FROM AIFeedback f " +
            "WHERE f.studentId = :studentId AND f.isCorrect = true " +
            "GROUP BY f.topic ORDER BY cnt DESC")
    List<Object[]> findStrongTopicsByStudentId(@Param("studentId") Long studentId);

    /**
     * Lấy N feedback gần nhất của học sinh
     * Dùng để AI đọc context lịch sử
     */
    List<AIFeedback> findTop10ByStudentIdOrderByCreatedAtDesc(Long studentId);

    /**
     * Đếm số câu đúng theo độ khó
     */
    @Query("SELECT f.difficulty, COUNT(f) FROM AIFeedback f " +
            "WHERE f.studentId = :studentId AND f.isCorrect = true " +
            "GROUP BY f.difficulty")
    List<Object[]> countCorrectByDifficulty(@Param("studentId") Long studentId);
}
