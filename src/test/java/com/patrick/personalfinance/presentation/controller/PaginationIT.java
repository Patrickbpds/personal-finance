package com.patrick.personalfinance.presentation.controller;

import com.patrick.personalfinance.support.ApiIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaginationIT extends ApiIntegrationTest {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Test
    @DisplayName("GET /api/titles uses page size 20 and a stable default ordering")
    void titlesUseDefaultPaginationAndOrder() throws Exception {
        String token = registerAndLogin("pagination-titles@example.com", "secret123");
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        createTitle(token, "First", "10.00", now.plusDays(5));
        createTitle(token, "Second", "20.00", now.plusDays(4));
        createTitle(token, "Third", "30.00", now.plusDays(3));
        createTitle(token, "Fourth", "40.00", now.plusDays(2));
        createTitle(token, "Fifth", "50.00", now.plusDays(1));

        mockMvc.perform(get("/api/titles").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.content[0].description").value("Fifth"))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    @DisplayName("GET /api/titles returns the requested slice with metadata")
    void titlesReturnRequestedSlice() throws Exception {
        String token = registerAndLogin("pagination-slices@example.com", "secret123");
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        createTitle(token, "First", "10.00", now.plusDays(5));
        createTitle(token, "Second", "20.00", now.plusDays(4));
        createTitle(token, "Third", "30.00", now.plusDays(3));
        createTitle(token, "Fourth", "40.00", now.plusDays(2));
        createTitle(token, "Fifth", "50.00", now.plusDays(1));

        mockMvc.perform(get("/api/titles")
                        .header("Authorization", bearer(token))
                        .param("page", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].description").value("Third"))
                .andExpect(jsonPath("$.content[1].description").value("Second"))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(3));
    }

    @Test
    @DisplayName("GET /api/titles respects the requested sort")
    void titlesRespectRequestedSort() throws Exception {
        String token = registerAndLogin("pagination-sort@example.com", "secret123");
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        createTitle(token, "Small", "10.00", now.plusDays(1));
        createTitle(token, "Large", "50.00", now.plusDays(2));
        createTitle(token, "Medium", "30.00", now.plusDays(3));

        mockMvc.perform(get("/api/titles")
                        .header("Authorization", bearer(token))
                        .param("sort", "value,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].value").value(50.00))
                .andExpect(jsonPath("$.content[1].value").value(30.00))
                .andExpect(jsonPath("$.content[2].value").value(10.00));
    }

    @Test
    @DisplayName("GET /api/center-costs is paginated and ordered by description")
    void centerCostsArePaginated() throws Exception {
        String token = registerAndLogin("pagination-centers@example.com", "secret123");
        createCenterCost(token, "Charlie");
        createCenterCost(token, "Alpha");
        createCenterCost(token, "Bravo");

        mockMvc.perform(get("/api/center-costs")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[0].description").value("Alpha"))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(3));

        mockMvc.perform(get("/api/center-costs")
                        .header("Authorization", bearer(token))
                        .param("page", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].description").value("Charlie"))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    private void createTitle(String token, String description, String value, LocalDateTime expirationDate) throws Exception {
        String body = """
                {"description":"%s","type":"RECEIVABLE","value":%s,"expirationDate":"%s"}
                """.formatted(description, value, FORMATTER.format(expirationDate));

        mockMvc.perform(post("/api/titles")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    private void createCenterCost(String token, String description) throws Exception {
        String body = """
                {"description":"%s"}
                """.formatted(description);

        mockMvc.perform(post("/api/center-costs")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }
}
