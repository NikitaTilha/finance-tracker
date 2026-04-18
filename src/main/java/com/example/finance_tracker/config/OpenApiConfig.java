package com.example.finance_tracker.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI financeTrackerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Finance Tracker API")
                        .version("1.0.0")
                        .description("REST API для финансового трекера")
                        .contact(new Contact()
                                .name("NikitaTilha")))
                .externalDocs(new ExternalDocumentation()
                        .description("Project repository")
                        .url("https://github.com/NikitaTilha/finance-tracker"));
    }
}