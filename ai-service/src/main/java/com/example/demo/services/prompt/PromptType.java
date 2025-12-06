package com.example.demo.services.prompt;

/**
 * Enum defining the types of prompts that can be built.
 * Each type corresponds to a specific AI task.
 */
public enum PromptType {
    FEEDBACK_GENERATION("feedback_generation"),
    HINT_GENERATION("hint_generation"),
    MATERIAL_EXPLANATION("material_explanation"),
    MATERIAL_RECOMMENDATION("material_recommendation");

    private final String value;

    PromptType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
