package com.studymate.module.ai.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studymate.module.ai.config.AiProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

@Component
public class DefaultAiClient implements AiClient {

    private final RestClient restClient;
    private final AiProperties aiProperties;
    private final ObjectMapper objectMapper;

    @Autowired
    public DefaultAiClient(RestClient.Builder restClientBuilder, AiProperties aiProperties) {
        this(restClientBuilder, aiProperties, new ObjectMapper());
    }

    DefaultAiClient(RestClient.Builder restClientBuilder, AiProperties aiProperties, ObjectMapper objectMapper) {
        this.restClient = restClientBuilder.build();
        this.aiProperties = aiProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    public String chat(String prompt) {
        validateConfig();
        Map<String, Object> requestBody = buildRequestBody(prompt);
        try {
            return restClient.post()
                    .uri(aiProperties.getApiUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(headers -> addAuthHeader(headers, aiProperties.getApiKey()))
                    .body(requestBody)
                    .exchange((request, response) -> {
                        String responseBody = response.bodyTo(String.class);
                        if (!response.getStatusCode().is2xxSuccessful()) {
                            String message = response.getStatusCode().value() == 429
                                    ? "AI service rate limited"
                                    : "AI service returned non-2xx response";
                            throw new AiClientException(message + ": " + responseBody, responseBody);
                        }
                        return extractAssistantContent(responseBody);
                    });
        } catch (AiClientException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new AiClientException("AI service request failed: " + exception.getMessage(), exception);
        } catch (RuntimeException exception) {
            throw new AiClientException("AI service request failed: " + exception.getMessage(), exception);
        }
    }

    private void validateConfig() {
        if (!StringUtils.hasText(aiProperties.getApiUrl())) {
            throw new AiClientException("AI apiUrl is not configured");
        }
        if (!StringUtils.hasText(aiProperties.getApiKey())) {
            throw new AiClientException("AI apiKey is not configured");
        }
    }

    private Map<String, Object> buildRequestBody(String prompt) {
        return Map.of(
                "model", aiProperties.getModel(),
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.2
        );
    }

    private void addAuthHeader(HttpHeaders headers, String apiKey) {
        headers.setBearerAuth(apiKey);
    }

    private String extractAssistantContent(String responseBody) {
        if (!StringUtils.hasText(responseBody)) {
            return "";
        }
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode contentNode = root.path("choices").path(0).path("message").path("content");
            if (contentNode.isTextual()) {
                return contentNode.asText();
            }
        } catch (Exception ignored) {
            return responseBody;
        }
        return responseBody;
    }
}
