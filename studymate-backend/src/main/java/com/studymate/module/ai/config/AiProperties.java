package com.studymate.module.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "studymate.ai")
public class AiProperties {

    private boolean mockEnabled = true;

    private String apiUrl = "";

    private String apiKey = "";

    private String model = "mock";

    private int timeoutMs = 10000;
}
