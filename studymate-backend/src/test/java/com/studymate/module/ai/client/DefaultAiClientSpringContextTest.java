package com.studymate.module.ai.client;

import com.studymate.module.ai.config.AiProperties;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultAiClientSpringContextTest {

    @Test
    void springCanCreateDefaultAiClientWithConstructorInjection() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean(AiProperties.class);
            context.registerBean(RestClient.Builder.class, () -> RestClient.builder());
            context.registerBean(DefaultAiClient.class);

            context.refresh();

            assertThat(context.getBean(AiClient.class)).isInstanceOf(DefaultAiClient.class);
        }
    }
}
