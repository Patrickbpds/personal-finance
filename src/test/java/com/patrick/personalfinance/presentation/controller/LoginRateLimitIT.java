package com.patrick.personalfinance.presentation.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.patrick.personalfinance.support.ApiIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LoginRateLimitIT extends ApiIntegrationTest {

    private static final String PASSWORD = "secret123";
    private static final int MAX_ATTEMPTS = 5;

    @Test
    @DisplayName("the sixth failed login in a row is rejected with 429 and Retry-After")
    void blocksAfterTooManyFailedAttempts() throws Exception {
        String email = "ratelimit-blocked@example.com";
        register(email, PASSWORD);

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            loginAttempt(email, "wrong-password", "203.0.113.10")
                    .andExpect(status().isUnauthorized());
        }

        MvcResult blocked = loginAttempt(email, PASSWORD, "203.0.113.10")
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.status").value(429))
                .andExpect(jsonPath("$.message").value("Too many login attempts. Try again later"))
                .andReturn();

        String retryAfter = blocked.getResponse().getHeader(HttpHeaders.RETRY_AFTER);
        assertThat(retryAfter).isNotBlank();
        assertThat(Long.parseLong(retryAfter)).isPositive();
    }

    @Test
    @DisplayName("a successful login clears the attempts of that email")
    void successfulLoginResetsTheCounter() throws Exception {
        String email = "ratelimit-reset@example.com";
        register(email, PASSWORD);

        for (int attempt = 1; attempt <= MAX_ATTEMPTS - 1; attempt++) {
            loginAttempt(email, "wrong-password", "203.0.113.20")
                    .andExpect(status().isUnauthorized());
        }

        loginAttempt(email, PASSWORD, "203.0.113.20")
                .andExpect(status().isOk());

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            loginAttempt(email, "wrong-password", "203.0.113.20")
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    @DisplayName("attempts are tracked per email, so one account does not lock another")
    void attemptsAreIsolatedByEmail() throws Exception {
        String blockedEmail = "ratelimit-first@example.com";
        String otherEmail = "ratelimit-second@example.com";
        register(blockedEmail, PASSWORD);
        register(otherEmail, PASSWORD);

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            loginAttempt(blockedEmail, "wrong-password", "203.0.113.30")
                    .andExpect(status().isUnauthorized());
        }

        loginAttempt(blockedEmail, PASSWORD, "203.0.113.30")
                .andExpect(status().isTooManyRequests());

        loginAttempt(otherEmail, PASSWORD, "203.0.113.30")
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("the error body follows the standard error contract")
    void blockedLoginUsesTheStandardErrorContract() throws Exception {
        String email = "ratelimit-contract@example.com";
        register(email, PASSWORD);

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            loginAttempt(email, "wrong-password", "203.0.113.40")
                    .andExpect(status().isUnauthorized());
        }

        MvcResult blocked = loginAttempt(email, PASSWORD, "203.0.113.40")
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(429))
                .andExpect(jsonPath("$.error").value("Too Many Requests"))
                .andExpect(jsonPath("$.path").value("/auth/login"))
                .andReturn();

        JsonNode body = objectMapper.readTree(blocked.getResponse().getContentAsString());
        assertThat(body.has("message")).isTrue();
    }

    private ResultActions loginAttempt(
            String email, String password, String clientIp) throws Exception {
        String body = """
                {"email":"%s","password":"%s"}
                """.formatted(email, password);

        MockHttpServletRequestBuilder request = post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .with(httpRequest -> {
                    httpRequest.setRemoteAddr(clientIp);
                    return httpRequest;
                });

        return mockMvc.perform(request);
    }
}
