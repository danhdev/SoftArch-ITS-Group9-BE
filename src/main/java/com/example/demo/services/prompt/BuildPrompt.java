package com.example.demo.services.prompt;

/**
 * Interface for building prompts for AI tasks.
 * Follows the Strategy Pattern - each implementation provides a different prompt building strategy.
 * 
 * @param <T> the type of context data used to build the prompt
 */
public interface BuildPrompt<T> {

    /**
     * Build a prompt string based on the provided context.
     *
     * @param context the context data used to build the prompt
     * @return the constructed prompt string
     */
    String buildPrompt(T context);

    /**
     * Get the type of prompt this builder creates.
     *
     * @return the prompt type
     */
    PromptType getPromptType();
}
