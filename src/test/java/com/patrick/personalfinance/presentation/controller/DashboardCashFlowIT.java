package com.patrick.personalfinance.presentation.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.patrick.personalfinance.support.ApiIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DashboardCashFlowIT extends ApiIntegrationTest {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Test
    @DisplayName("the dashboard aggregates cash flow and payment status in SQL")
    void aggregatesCashFlow() throws Exception {
        String token = registerAndLogin("dashboard@example.com", "secret123");
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        createTitle(token, "Paid receivable", "RECEIVABLE", "100.00",
                now.plusDays(30), now.minusDays(1));
        createTitle(token, "Overdue payable", "PAYABLE", "40.00",
                now.minusDays(30), null);
        createTitle(token, "Pending receivable", "RECEIVABLE", "50.00",
                now.plusDays(60), null);
        createTitle(token, "Pending payable", "PAYABLE", "10.00",
                now.plusDays(90), null);

        MvcResult result = cashFlow(token, now.minusDays(365), now.plusDays(365))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalReceivable").value(150.00))
                .andExpect(jsonPath("$.totalPayable").value(50.00))
                .andExpect(jsonPath("$.balance").value(100.00))
                .andExpect(jsonPath("$.totalPaid").value(100.00))
                .andExpect(jsonPath("$.totalOverdue").value(40.00))
                .andExpect(jsonPath("$.totalPending").value(60.00))
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());

        assertThat(statuses(body.get("receivableTitles")))
                .containsExactlyInAnyOrder("PAID", "PENDING");
        assertThat(statuses(body.get("payableTitles")))
                .containsExactlyInAnyOrder("OVERDUE", "PENDING");
    }

    @Test
    @DisplayName("only titles inside the expiration window are aggregated")
    void filtersByExpirationDateRange() throws Exception {
        String token = registerAndLogin("dashboard-range@example.com", "secret123");
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        createTitle(token, "Inside", "RECEIVABLE", "25.00", now.plusDays(10), null);
        createTitle(token, "Outside", "RECEIVABLE", "75.00", now.plusDays(400), null);

        cashFlow(token, now.minusDays(1), now.plusDays(30))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalReceivable").value(25.00))
                .andExpect(jsonPath("$.receivableTitles.length()").value(1));
    }

    @Test
    @DisplayName("an inverted date range is rejected")
    void rejectsInvertedDateRange() throws Exception {
        String token = registerAndLogin("dashboard-inverted@example.com", "secret123");
        LocalDateTime now = LocalDateTime.now();

        cashFlow(token, now, now.minusDays(1))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("startDate must not be after endDate"));
    }

    private void createTitle(
            String token,
            String description,
            String type,
            String value,
            LocalDateTime expirationDate,
            LocalDateTime paymentDate
    ) throws Exception {
        String payment = paymentDate == null ? "null" : "\"" + FORMATTER.format(paymentDate) + "\"";
        String body = """
                {"description":"%s","type":"%s","value":%s,
                 "expirationDate":"%s","paymentDate":%s}
                """.formatted(description, type, value, FORMATTER.format(expirationDate), payment);

        mockMvc.perform(post("/api/titles")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    private ResultActions cashFlow(
            String token, LocalDateTime startDate, LocalDateTime endDate) throws Exception {
        return mockMvc.perform(get("/api/dashboard/cash-flow")
                .header("Authorization", bearer(token))
                .param("startDate", FORMATTER.format(startDate))
                .param("endDate", FORMATTER.format(endDate)));
    }

    private List<String> statuses(JsonNode titles) {
        return StreamSupport.stream(titles.spliterator(), false)
                .map(title -> title.get("paymentStatus").asText())
                .toList();
    }
}
