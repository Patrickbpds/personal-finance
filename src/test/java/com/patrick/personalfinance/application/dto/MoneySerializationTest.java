package com.patrick.personalfinance.application.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.patrick.personalfinance.application.dto.responses.DashboardResponseDto;
import com.patrick.personalfinance.application.dto.responses.TitleResponseDto;
import com.patrick.personalfinance.domain.enums.TypeTitle;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MoneySerializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void serializesTitleValueWithTwoDecimals() throws Exception {
        TitleResponseDto dto = new TitleResponseDto(
                UUID.fromString("00000000-0000-0000-0000-000000000001"),
                "Salary",
                TypeTitle.RECEIVABLE,
                List.of(),
                new BigDecimal("100.50"),
                null,
                null,
                null,
                null,
                null
        );

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"value\":100.50");
    }

    @Test
    void serializesDashboardTotalsWithTwoDecimals() throws Exception {
        DashboardResponseDto dto = new DashboardResponseDto(
                new BigDecimal("0.00"),
                new BigDecimal("1234.50"),
                new BigDecimal("1234.50"),
                List.of(),
                List.of()
        );

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"totalPayable\":0.00");
        assertThat(json).contains("\"totalReceivable\":1234.50");
        assertThat(json).contains("\"balance\":1234.50");
    }
}
