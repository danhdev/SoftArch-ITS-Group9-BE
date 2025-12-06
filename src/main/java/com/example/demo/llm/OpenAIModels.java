package com.example.demo.llm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTOs for OpenAI Chat Completion API requests and responses.
 */
public class OpenAIModels {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChatCompletionRequest {
        private String model;
        private List<Message> messages;
        private Double temperature;
        
        @JsonProperty("max_tokens")
        private Integer maxTokens;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Message {
        private String role;
        private String content;
        
        public static Message system(String content) {
            return new Message("system", content);
        }
        
        public static Message user(String content) {
            return new Message("user", content);
        }
        
        public static Message assistant(String content) {
            return new Message("assistant", content);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatCompletionResponse {
        private String id;
        private String object;
        private Long created;
        private String model;
        private List<Choice> choices;
        private Usage usage;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Choice {
        private Integer index;
        private Message message;
        
        @JsonProperty("finish_reason")
        private String finishReason;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Usage {
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;
        
        @JsonProperty("completion_tokens")
        private Integer completionTokens;
        
        @JsonProperty("total_tokens")
        private Integer totalTokens;
    }
}
