package com.patrick.personalfinance.presentation.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.patrick.personalfinance.support.ApiIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserAccountSecurityIT extends ApiIntegrationTest {

    @Test
    @DisplayName("the former public /auth/update route no longer exists")
    void authUpdateRouteIsGone() throws Exception {
        mockMvc.perform(put("/auth/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Hacker","email":"victim@example.com","password":"newpass123"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("legacy /api/users routes that exposed other accounts are gone")
    void legacyUserRoutesAreGone() throws Exception {
        String token = registerAndLogin("legacy@example.com", "secret123");

        mockMvc.perform(get("/api/users").header("Authorization", bearer(token)))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/users/00000000-0000-0000-0000-000000000000")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/users/00000000-0000-0000-0000-000000000000")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("/api/users/me requires authentication")
    void meRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("/api/users/me returns only the authenticated user")
    void meReturnsOwnProfile() throws Exception {
        String token = registerAndLogin("self@example.com", "secret123");

        mockMvc.perform(get("/api/users/me").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("self@example.com"));
    }

    @Test
    @DisplayName("updating the profile changes only the authenticated user")
    void updateProfileChangesOwnAccount() throws Exception {
        String token = registerAndLogin("updater@example.com", "secret123");

        mockMvc.perform(put("/api/users/me")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"New Name"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.email").value("updater@example.com"));
    }

    @Test
    @DisplayName("changing the password requires the current password")
    void passwordChangeRequiresCurrentPassword() throws Exception {
        String token = registerAndLogin("pwd@example.com", "secret123");

        mockMvc.perform(put("/api/users/me")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Pwd User","newPassword":"brandNew123"}
                                """))
                .andExpect(status().isUnauthorized());

        login("pwd@example.com", "secret123");

        mockMvc.perform(put("/api/users/me")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Pwd User","currentPassword":"secret123","newPassword":"brandNew123"}
                                """))
                .andExpect(status().isOk());

        login("pwd@example.com", "brandNew123");
    }

    @Test
    @DisplayName("deactivating the account prevents further logins")
    void deactivateAccount() throws Exception {
        String token = registerAndLogin("gone@example.com", "secret123");

        mockMvc.perform(delete("/api/users/me").header("Authorization", bearer(token)))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"gone@example.com","password":"secret123"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("user A cannot see or touch user B titles")
    void titlesAreIsolatedBetweenUsers() throws Exception {
        String tokenA = registerAndLogin("alice@example.com", "secret123");
        String tokenB = registerAndLogin("bob@example.com", "secret123");

        MvcResult created = mockMvc.perform(post("/api/titles")
                        .header("Authorization", bearer(tokenA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "description":"Alice private title",
                                  "type":"RECEIVABLE",
                                  "value":100.00,
                                  "expirationDate":"2026-12-31T00:00:00"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        String titleId = objectMapper
                .readTree(created.getResponse().getContentAsString())
                .get("id").asText();

        mockMvc.perform(get("/api/titles").header("Authorization", bearer(tokenB)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        mockMvc.perform(get("/api/titles/" + titleId).header("Authorization", bearer(tokenB)))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/titles/" + titleId).header("Authorization", bearer(tokenB)))
                .andExpect(status().isNotFound());

        MvcResult aliceList = mockMvc.perform(get("/api/titles")
                        .header("Authorization", bearer(tokenA)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode aliceTitles = objectMapper.readTree(aliceList.getResponse().getContentAsString());
        assertThat(aliceTitles).hasSize(1);
        assertThat(aliceTitles.get(0).get("description").asText()).isEqualTo("Alice private title");
    }

    @Test
    @DisplayName("user A cannot see or touch user B center costs")
    void centerCostsAreIsolatedBetweenUsers() throws Exception {
        String tokenA = registerAndLogin("carol@example.com", "secret123");
        String tokenB = registerAndLogin("dave@example.com", "secret123");

        MvcResult created = mockMvc.perform(post("/api/center-costs")
                        .header("Authorization", bearer(tokenA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"description":"Carol private center cost"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        String centerCostId = objectMapper
                .readTree(created.getResponse().getContentAsString())
                .get("id").asText();

        mockMvc.perform(get("/api/center-costs").header("Authorization", bearer(tokenB)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        mockMvc.perform(get("/api/center-costs/" + centerCostId)
                        .header("Authorization", bearer(tokenB)))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/center-costs/" + centerCostId)
                        .header("Authorization", bearer(tokenB)))
                .andExpect(status().isNotFound());

        MvcResult carolList = mockMvc.perform(get("/api/center-costs")
                        .header("Authorization", bearer(tokenA)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode carolCenterCosts = objectMapper.readTree(carolList.getResponse().getContentAsString());
        assertThat(carolCenterCosts).hasSize(1);
        assertThat(carolCenterCosts.get(0).get("description").asText())
                .isEqualTo("Carol private center cost");
    }
}
