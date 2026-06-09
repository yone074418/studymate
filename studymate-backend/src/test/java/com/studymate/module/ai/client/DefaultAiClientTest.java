package com.studymate.module.ai.client;

import com.studymate.module.ai.config.AiProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

class DefaultAiClientTest {

    private AiProperties properties;
    private MockRestServiceServer server;
    private AiClient aiClient;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        properties = new AiProperties();
        properties.setApiUrl("https://api.example.test/chat");
        properties.setApiKey("test-key");
        properties.setModel("test-model");
        aiClient = new DefaultAiClient(builder, properties);
    }

    @Test
    void chatReturnsOpenAiCompatibleMessageContent() {
        server.expect(requestTo("https://api.example.test/chat"))
                .andExpect(header(AUTHORIZATION, "Bearer test-key"))
                .andExpect(jsonPath("$.model").value("test-model"))
                .andRespond(withSuccess("""
                        {"choices":[{"message":{"content":"{\\"durationMinutes\\":30}"}}]}
                        """, MediaType.APPLICATION_JSON));

        String response = aiClient.chat("prompt text");

        assertThat(response).isEqualTo("{\"durationMinutes\":30}");
        server.verify();
    }

    @Test
    void chatThrowsAiClientExceptionForRateLimit() {
        server.expect(requestTo("https://api.example.test/chat"))
                .andRespond(withStatus(TOO_MANY_REQUESTS).body("rate limited"));

        assertThatThrownBy(() -> aiClient.chat("prompt text"))
                .isInstanceOf(AiClientException.class)
                .hasMessageContaining("rate limited");
    }

    @Test
    void chatThrowsAiClientExceptionForNon2xxResponse() {
        server.expect(requestTo("https://api.example.test/chat"))
                .andRespond(withServerError().body("server error"));

        assertThatThrownBy(() -> aiClient.chat("prompt text"))
                .isInstanceOf(AiClientException.class)
                .hasMessageContaining("AI service returned non-2xx");
    }
}
