package com.example.AIservice;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		// Load .env file and set environment variables
		try {
			Dotenv dotenv = Dotenv.configure()
					.directory("./")
					.ignoreIfMissing()
					.load();

			// Set environment variables from .env file
			dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())
			);
		} catch (Exception e) {
			System.out.println("Warning: Could not load .env file. Using default configuration.");
		}

		SpringApplication.run(Application.class, args);
	}

}
