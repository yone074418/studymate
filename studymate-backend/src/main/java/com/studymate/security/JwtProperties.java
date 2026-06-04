package com.studymate.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "studymate.jwt")
public class JwtProperties {

    private String secret = "studymate-default-secret-change-me-in-production";

    private Long expirationSeconds = 86400L;
}
