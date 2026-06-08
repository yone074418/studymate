package com.studymate.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private JwtProperties jwtProperties;
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setSecret("test-secret-that-is-long-enough-for-hmac-signing");
        jwtProperties.setExpirationSeconds(3600L);
        jwtUtil = new JwtUtil(jwtProperties);
    }

    @Test
    void generatedTokenCanBeParsedAndValidated() {
        String token = jwtUtil.generateToken(7L, "alice");

        assertThat(jwtUtil.validateToken(token)).isTrue();
        assertThat(jwtUtil.getUserId(token)).isEqualTo(7L);
        assertThat(jwtUtil.getUsername(token)).isEqualTo("alice");
    }

    @Test
    void invalidTokenReturnsValidationFailure() {
        assertThat(jwtUtil.validateToken("bad-token")).isFalse();
    }

    @Test
    void expiredTokenReturnsValidationFailure() throws InterruptedException {
        jwtProperties.setExpirationSeconds(0L);
        String token = jwtUtil.generateToken(7L, "alice");

        Thread.sleep(1100L);

        assertThat(jwtUtil.validateToken(token)).isFalse();
    }
}
