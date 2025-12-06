package com.example.demo.services.material;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.material.ChapterDTO;
import com.example.demo.dto.material.MaterialApiResponse;
import com.example.demo.dto.material.MaterialDTO;
import com.example.demo.dto.request.AIMaterialRequest;
import com.example.demo.dto.request.AIFeedbackRequest;
import com.example.demo.dto.AIResponse;
import com.example.demo.models.MaterialRecommendationRecord;
import com.example.demo.proxy.MaterialProxyClient;
import com.example.demo.repository.MaterialRecommendationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of MaterialService.
 * Follows Single Responsibility Principle - handles only material-related operations.
 * Uses Strategy Pattern via AI Tasks for different operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
// act as a facade for material-related operations
public class MaterialServiceImpl implements IMaterialService {

    private final MaterialRecommendationTask recommendationTask;
    private final MaterialExplanationTask explanationTask;
    private final MaterialProxyClient materialProxyClient;
    private final MaterialRecommendationRepository recommendationRepository;

    @Override
    public AIResponse recommend(AIMaterialRequest request) {
        log.info("Processing material recommendation request for student: {}, course: {}", 
                request.getStudentId(), request.getCourseId());

        // Fetch all chapters for the course
        List<ChapterDTO> chapters = fetchChaptersByCourse(request.getCourseId());
        log.info("Fetched {} chapters for course: {}", chapters.size(), request.getCourseId());

        // Fetch all materials from each chapter
        List<MaterialDTO> allMaterials = fetchMaterialsForChapters(chapters);
        log.info("Fetched {} total materials from {} chapters", allMaterials.size(), chapters.size());

        // Execute the recommendation task with materials context
        AIResponse response = recommendationTask.execute(request, chapters, allMaterials);

        // Save recommendation record
        saveRecommendationRecord(request, response);

        return response;
    }

    @Override
    public AIResponse explain(AIFeedbackRequest request) {
        log.info("Processing material explanation request");
        return explanationTask.execute(request);
    }

    /**
     * Fetch all chapters for a given course from the external API.
     *
     * @param courseId the course ID
     * @return list of chapters
     */
    private List<ChapterDTO> fetchChaptersByCourse(String courseId) {
        try {
            MaterialApiResponse<List<ChapterDTO>> response = materialProxyClient.getChaptersByCourse(courseId);
            if (response != null && response.getData() != null) {
                return response.getData();
            }
            log.warn("No chapters found for course: {}", courseId);
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("Error fetching chapters for course: {}", courseId, e);
            return new ArrayList<>();
        }
    }

    /**
     * Fetch all materials from all chapters.
     *
     * @param chapters list of chapters
     * @return aggregated list of all materials
     */
    private List<MaterialDTO> fetchMaterialsForChapters(List<ChapterDTO> chapters) {
        List<MaterialDTO> allMaterials = new ArrayList<>();

        for (ChapterDTO chapter : chapters) {
            try {
                String chapterId = chapter.getChapterId();
                if (chapterId == null || chapterId.isEmpty()) {
                    log.warn("Chapter has no ID, skipping: {}", chapter.getTitle());
                    continue;
                }

                MaterialApiResponse<List<MaterialDTO>> response = materialProxyClient.getMaterialsByChapter(chapterId);
                if (response != null && response.getData() != null) {
                    allMaterials.addAll(response.getData());
                    log.debug("Fetched {} materials for chapter: {}", response.getData().size(), chapter.getTitle());
                }
            } catch (Exception e) {
                log.error("Error fetching materials for chapter: {}", chapter.getTitle(), e);
            }
        }

        return allMaterials;
    }

    /**
     * Save the recommendation record to the database.
     *
     * @param request  the original request
     * @param response the AI response
     */
    private void saveRecommendationRecord(AIMaterialRequest request, AIResponse response) {
        try {
            MaterialRecommendationRecord record = MaterialRecommendationRecord.builder()
                    .studentId(request.getStudentId())
                    .courseId(request.getCourseId())
                    .recommendationText(response.getResult())
                    .preferredDifficulty(request.getPreferredDifficulty())
                    .preferredType(request.getPreferredType())
                    .createdAt(LocalDateTime.now())
                    .build();

            recommendationRepository.save(record);
            log.info("Saved recommendation record for student: {}", request.getStudentId());
        } catch (Exception e) {
            log.error("Error saving recommendation record", e);
        }
    }
}
