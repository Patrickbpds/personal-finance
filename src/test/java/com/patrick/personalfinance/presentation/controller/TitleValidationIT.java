package com.patrick.personalfinance.presentation.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.patrick.personalfinance.support.ApiIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TitleValidationIT extends ApiIntegrationTest {

    @Test
    @DisplayName("Bean Validation rejects a title missing the required fields")
    void rejectsMissingRequiredFields() throws Exception {
        String token = registerAndLogin("title-missing@example.com", "secret123");

        createTitle(token, "{}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Bean Validation rejects a non-positive value")
    void rejectsNonPositiveValue() throws Exception {
        String token = registerAndLogin("title-value@example.com", "secret123");

        createTitle(token, """
                {"description":"Salary","type":"RECEIVABLE","value":0,
                 "expirationDate":"2026-12-31T00:00:00"}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Value must be positive")));
    }

    @Test
    @DisplayName("Bean Validation rejects a value with more than two decimals")
    void rejectsMoreThanTwoDecimals() throws Exception {
        String token = registerAndLogin("title-decimals@example.com", "secret123");

        createTitle(token, """
                {"description":"Salary","type":"RECEIVABLE","value":10.123,
                 "expirationDate":"2026-12-31T00:00:00"}
                """)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("the registration date is always set by the server")
    void ignoresClientProvidedRegistrationDate() throws Exception {
        String token = registerAndLogin("title-date@example.com", "secret123");
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        MvcResult result = createTitle(token, """
                {"description":"Salary","type":"RECEIVABLE","value":1500,
                 "registrationDate":"2000-01-01T00:00:00",
                 "expirationDate":"2026-12-31T00:00:00"}
                """)
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        LocalDateTime registrationDate = LocalDateTime.parse(body.get("registrationDate").asText());

        assertThat(registrationDate).isAfter(before);
        assertThat(registrationDate.isBefore(LocalDateTime.now().plusSeconds(1))).isTrue();
    }

    @Test
    @DisplayName("a valid title is created with the enum name and normalized value")
    void createsValidTitle() throws Exception {
        String token = registerAndLogin("title-valid@example.com", "secret123");

        createTitle(token, """
                {"description":"Salary","type":"RECEIVABLE","value":1500,
                 "expirationDate":"2026-12-31T00:00:00"}
                """)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("RECEIVABLE"))
                .andExpect(jsonPath("$.value").value(1500.00))
                .andExpect(jsonPath("$.description").value("Salary"));
    }

    private ResultActions createTitle(String token, String body) throws Exception {
        return mockMvc.perform(post("/api/titles")
                .header("Authorization", bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }
}
