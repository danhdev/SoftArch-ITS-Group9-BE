package com.example.course.service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI courseServiceAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Course Service API")
                        .description("API documentation for Course Service - Group 9")
                        .version("1.0.0"));
    }
}
