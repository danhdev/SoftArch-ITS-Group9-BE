package org.example.ai;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main Spring Boot Application class.
 * 
 * Configuration:
 * - @SpringBootApplication(scanBasePackages): Scan components in both packages
 * - @EnableJpaRepositories: Enable JPA repositories in aifeedback package
 * - @EntityScan: Scan JPA entities in aifeedback.domain package
 */
@SpringBootApplication(scanBasePackages = { "org.example.ai", "com.example.its.aifeedback" })
@EnableJpaRepositories(basePackages = "com.example.its.aifeedback.repository")
@EntityScan(basePackages = "com.example.its.aifeedback.domain")
public class AiApplication {

    public static void main(String[] args) {
        // Load .env file BEFORE Spring starts
        loadEnvFile();

        SpringApplication.run(AiApplication.class, args);
    }

    /**
     * Load environment variables from .env file.
     * This must happen before Spring context initialization
     * so that @Value annotations can access these properties.
     */
    private static void loadEnvFile() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            for (DotenvEntry entry : dotenv.entries()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (System.getProperty(key) == null && System.getenv(key) == null) {
                    System.setProperty(key, value);
                }
            }

            System.out.println("[AiApplication] Loaded .env file successfully");
        } catch (Exception e) {
            System.err.println("[AiApplication] Warning: Could not load .env: " + e.getMessage());
        }
    }
}
