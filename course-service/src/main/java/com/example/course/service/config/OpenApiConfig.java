package com.example.course.service.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {

    @Value("${springdoc.server.url}")
    private String serverUrl;

    @Bean
    public OpenAPI defineOpenApi() {
        Server server = new Server();
        server.setUrl(serverUrl);
        server.setDescription("Development");

        Info information = new Info()
                .title("Course Service")
                .version("1.0")
                .description("Here is the API documentation for the Course Service");


        return new OpenAPI()
                .info(information)
                .servers(List.of(server));
    }

}
