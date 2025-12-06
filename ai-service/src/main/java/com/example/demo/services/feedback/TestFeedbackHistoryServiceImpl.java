package com.example.demo.services.feedback;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.models.FeedbackRecord;
import com.example.demo.repository.FeedbackHistoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of FeedbackHistoryService.
 * Follows Single Responsibility Principle - handles only feedback history persistence.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TestFeedbackHistoryServiceImpl implements ITestFeedbackHistoryService {

    private final FeedbackHistoryRepository feedbackHistoryRepository;

    @Override
    public List<FeedbackRecord> getHistory(String studentId) {
        log.debug("Fetching feedback history for student: {}", studentId);
        return feedbackHistoryRepository.findByStudentId(studentId);
    }

    @Override
    public Optional<FeedbackRecord> getByAssessment(String studentId, String assessmentId) {
        log.debug("Fetching feedback for student: {}, assessment: {}", studentId, assessmentId);
        return feedbackHistoryRepository.findByStudentIdAndAssessmentId(studentId, assessmentId);
    }

    @Override
    public FeedbackRecord save(FeedbackRecord record) {
        log.debug("Saving feedback record for student: {}", record.getStudentId());
        return feedbackHistoryRepository.save(record);
    }
}
