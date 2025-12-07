package com.example.demo.llm;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * OpenAI implementation of LLMClient using Spring AI.
 * Follows Single Responsibility Principle - only handles OpenAI API communication.
 */
@Component
@Slf4j
public class OpenAIClientImpl implements LLMClient {

    private final ChatClient chatClient;

    @Value("${spring.ai.openai.api-key:}")
    private String apiKey;

    public OpenAIClientImpl(ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
    }

    @Override
    public String chat(String prompt) {
        log.info("Sending prompt to OpenAI via Spring AI");
        log.debug("Prompt length: {} characters", prompt.length());

        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("OpenAI API key not configured. Returning mock response.");
            return generateMockResponse(prompt);
        }

        try {
            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            log.info("Received response from OpenAI, length: {} characters",
                    response != null ? response.length() : 0);
            return response != null ? response : "No response generated.";

        } catch (Exception e) {
            log.error("Error calling OpenAI API: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get response from OpenAI: " + e.getMessage(), e);
        }
    }

    @Override
    public String chat(String systemMessage, String userPrompt) {
        log.info("Sending prompt with system message to OpenAI via Spring AI");

        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("OpenAI API key not configured. Returning mock response.");
            return generateMockResponse(userPrompt);
        }

        try {
            Prompt prompt = new Prompt(List.of(
                    new SystemMessage(systemMessage),
                    new UserMessage(userPrompt)
            ));

            String response = chatClient.prompt(prompt)
                    .call()
                    .content();

            log.info("Received response from OpenAI with system message, length: {} characters",
                    response != null ? response.length() : 0);
            return response != null ? response : "No response generated.";

        } catch (Exception e) {
            log.error("Error calling OpenAI API with system message: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get response from OpenAI: " + e.getMessage(), e);
        }
    }

    /**
     * Generate a mock response for development/testing purposes when API key is not configured.
     */
    private String generateMockResponse(String prompt) {
        if (prompt.toLowerCase().contains("feedback")) {
            return """
                    ## Feedback on Your Submission
                    
                    ### Overall Performance
                    Good effort on this assessment! You've demonstrated understanding of the core concepts.
                    
                    ### Strengths
                    - Clear understanding of basic concepts
                    - Good problem-solving approach
                    
                    ### Areas for Improvement
                    - Review the detailed explanations for questions you missed
                    - Practice similar problems to reinforce learning
                    
                    ### Study Recommendations
                    - Review Chapter 5: Core Concepts
                    - Complete Practice Set B for additional practice
                    
                    Keep up the good work! You're making progress.
                    """;
        }
        return "I've processed your request. Please let me know if you need any clarification.";
    }
}
