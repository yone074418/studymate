package com.studymate.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI studyMateOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("StudyMate API")
                        .description("StudyMate AI 学习复盘与 Java 实习备考陪伴系统接口文档")
                        .version("v1.0.0"));
    }
}
