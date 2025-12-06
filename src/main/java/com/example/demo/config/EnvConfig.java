package com.example.demo.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to load environment variables from .env file.
 * This provides a convenient way to manage sensitive configuration
 * (API keys, database credentials) without setting system environment
 * variables.
 * 
 * Usage: Place your .env file in the project root directory.
 * The values will be available via @Value("${KEY_NAME}") or
 * Environment.getProperty("KEY_NAME")
 * 
 * IMPORTANT: This uses a static initializer to ensure .env is loaded
 * BEFORE Spring context starts, so @Value annotations work correctly.
 */
@Configuration
public class EnvConfig {

    // Static block runs before Spring context initialization
    static {
        loadEnvFile();
    }

    /**
     * Load .env file and set as system properties.
     * Must run before Spring @Value injection happens.
     */
    private static void loadEnvFile() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing() // Don't fail if .env doesn't exist
                    .load();

            // Set all entries as system properties
            for (DotenvEntry entry : dotenv.entries()) {
                String key = entry.getKey();
                String value = entry.getValue();

                // Only set if not already defined (system env takes precedence)
                if (System.getProperty(key) == null && System.getenv(key) == null) {
                    System.setProperty(key, value);
                }
            }

            System.out.println("[EnvConfig] Loaded environment variables from .env file");
        } catch (Exception e) {
            System.err.println("[EnvConfig] Warning: Could not load .env file: " + e.getMessage());
        }
    }
}
