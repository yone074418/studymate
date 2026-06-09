package com.studymate.module.ai.config;

import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.time.Duration;

@Configuration
public class AiRestClientConfig {

    @Bean
    public RestClientCustomizer aiRestClientCustomizer(AiProperties aiProperties) {
        return restClientBuilder -> {
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            Duration timeout = Duration.ofMillis(Math.max(aiProperties.getTimeoutMs(), 1));
            requestFactory.setConnectTimeout(timeout);
            requestFactory.setReadTimeout(timeout);
            restClientBuilder.requestFactory(requestFactory);
        };
    }
}
