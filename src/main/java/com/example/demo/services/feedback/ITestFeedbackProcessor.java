package com.example.demo.services.feedback;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.example.demo.dto.FeedbackInsights;

import lombok.extern.slf4j.Slf4j;

/**
 * Processor for extracting insights from feedback text.
 * Follows Single Responsibility Principle - handles only feedback text processing.
 */
@Component
@Slf4j
public class ITestFeedbackProcessor {

    private static final Pattern STRENGTHS_PATTERN = Pattern.compile(
            "(?i)\\*\\*Strengths[:\\*]*\\*\\*([\\s\\S]*?)(?=\\*\\*|$)"
    );
    
    private static final Pattern WEAKNESSES_PATTERN = Pattern.compile(
            "(?i)\\*\\*(?:Areas for Improvement|Weaknesses)[:\\*]*\\*\\*([\\s\\S]*?)(?=\\*\\*|$)"
    );
    
    private static final Pattern RECOMMENDATIONS_PATTERN = Pattern.compile(
            "(?i)\\*\\*Recommendations[:\\*]*\\*\\*([\\s\\S]*?)(?=\\*\\*|$)"
    );

    /**
     * Extract insights from feedback text.
     *
     * @param feedbackText the raw feedback text
     * @return extracted insights
     */
    public FeedbackInsights extractInsights(String feedbackText) {
        log.debug("Extracting insights from feedback text");

        List<String> skillsImproved = extractSection(feedbackText, STRENGTHS_PATTERN);
        List<String> weakPoints = extractSection(feedbackText, WEAKNESSES_PATTERN);
        List<String> suggestedResources = extractSection(feedbackText, RECOMMENDATIONS_PATTERN);

        return FeedbackInsights.builder()
                .skillsImproved(skillsImproved)
                .weakPoints(weakPoints)
                .suggestedResources(suggestedResources)
                .build();
    }

    /**
     * Extract bullet points from a section of the feedback.
     */
    private List<String> extractSection(String text, Pattern pattern) {
        List<String> items = new ArrayList<>();
        
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String section = matcher.group(1);
            // Extract bullet points (lines starting with - or *)
            String[] lines = section.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("-") || line.startsWith("*")) {
                    String item = line.replaceFirst("^[-*]\\s*", "").trim();
                    if (!item.isEmpty()) {
                        items.add(item);
                    }
                }
            }
        }
        
        return items;
    }
}
