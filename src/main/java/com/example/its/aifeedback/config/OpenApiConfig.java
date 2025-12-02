package com.example.its.aifeedback.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI (Swagger) Configuration for AI Feedback Service.
 * 
 * Access Swagger UI at: http://localhost:8080/swagger-ui.html
 * Access API docs at: http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI Feedback Service API")
                        .version("1.0.0")
                        .description("""
                                API documentation for the AI Feedback Service module.

                                This service provides intelligent feedback generation for student submissions
                                in an Intelligent Tutoring System (ITS).

                                ## Features:
                                - Generate AI-powered feedback for student answers
                                - Retrieve feedback history
                                - Get learning recommendations

                                ## Architecture:
                                - Built with Spring Boot 3.3.5
                                - Follows SOLID principles
                                - Uses Strategy Pattern for AI Engine
                                """)
                        .contact(new Contact()
                                .name("AI Feedback Team")
                                .email("support@example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server")));
    }
}
