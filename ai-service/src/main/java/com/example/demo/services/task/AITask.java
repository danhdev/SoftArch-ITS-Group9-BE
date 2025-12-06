package com.example.demo.services.task;

import com.example.demo.dto.AIResponse;

/**
 * Interface for AI tasks following the Strategy Pattern.
 * Each implementation handles a specific type of AI operation.
 * 
 * Follows:
 * - Single Responsibility Principle (SRP): Each task handles one specific operation
 * - Open/Closed Principle (OCP): New tasks can be added without modifying existing code
 * - Liskov Substitution Principle (LSP): All implementations can be used interchangeably
 * - Interface Segregation Principle (ISP): Simple interface with single method
 * - Dependency Inversion Principle (DIP): High-level modules depend on this abstraction
 * 
 * @param <T> the type of request this task handles
 */
public interface AITask<T> {

    /**
     * Execute the AI task with the given request.
     *
     * @param request the AI request containing input and context
     * @return the AI response with results and metadata
     */
    AIResponse execute(T request);

    /**
     * Get the type of task this implementation handles.
     *
     * @return the task type identifier
     */
    String getTaskType();
}
