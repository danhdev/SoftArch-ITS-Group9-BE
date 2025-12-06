package com.example.demo.llm;

/**
 * Interface for LLM (Large Language Model) client abstraction.
 * Follows Dependency Inversion Principle (DIP) - high-level modules depend on this abstraction,
 * not on concrete implementations.
 * 
 * This allows easy swapping of LLM providers (OpenAI, Azure OpenAI, Anthropic, etc.)
 * without changing the consuming code.
 */
public interface LLMClient {

    /**
     * Send a chat prompt to the LLM and get a response.
     *
     * @param prompt the input prompt to send
     * @return the LLM's response
     */
    String chat(String prompt);

    /**
     * Send a chat prompt with a system message.
     *
     * @param systemMessage the system message to set context
     * @param prompt        the user prompt
     * @return the LLM's response
     */
    default String chat(String systemMessage, String prompt) {
        return chat(systemMessage + "\n\n" + prompt);
    }
}
