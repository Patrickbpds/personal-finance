package com.patrick.personalfinance.service;

import com.patrick.personalfinance.application.dto.responses.DashboardResponseDto;
import com.patrick.personalfinance.application.dto.responses.TitleResponseDto;
import com.patrick.personalfinance.domain.enums.TitleType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DashboardServiceTest {

    private final TitleService titleService = mock(TitleService.class);
    private final DashboardService dashboardService = new DashboardService(titleService);

    private final LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 0, 0);
    private final LocalDateTime endDate = LocalDateTime.of(2026, 12, 31, 23, 59);

    @Test
    void sumsValuesWithoutFloatingPointError() {
        when(titleService.getByExpirationDateRange(startDate, endDate)).thenReturn(List.of(
                title(TitleType.RECEIVABLE, "0.10"),
                title(TitleType.RECEIVABLE, "0.20"),
                title(TitleType.PAYABLE, "0.10")
        ));

        DashboardResponseDto result = dashboardService.getCashFlow(startDate, endDate);

        assertThat(result.totalReceivable()).isEqualTo(new BigDecimal("0.30"));
        assertThat(result.totalPayable()).isEqualTo(new BigDecimal("0.10"));
        assertThat(result.balance()).isEqualTo(new BigDecimal("0.20"));
    }

    @Test
    void returnsZeroTotalsScaledToTwoDecimalsWhenThereAreNoTitles() {
        when(titleService.getByExpirationDateRange(startDate, endDate)).thenReturn(List.of());

        DashboardResponseDto result = dashboardService.getCashFlow(startDate, endDate);

        assertThat(result.totalReceivable()).isEqualTo(new BigDecimal("0.00"));
        assertThat(result.totalPayable()).isEqualTo(new BigDecimal("0.00"));
        assertThat(result.balance()).isEqualTo(new BigDecimal("0.00"));
    }

    @Test
    void splitsTitlesByType() {
        TitleResponseDto receivable = title(TitleType.RECEIVABLE, "150.00");
        TitleResponseDto payable = title(TitleType.PAYABLE, "40.00");

        when(titleService.getByExpirationDateRange(startDate, endDate)).thenReturn(List.of(receivable, payable));

        DashboardResponseDto result = dashboardService.getCashFlow(startDate, endDate);

        assertThat(result.receivableTitles()).containsExactly(receivable);
        assertThat(result.payableTitles()).containsExactly(payable);
        assertThat(result.balance()).isEqualTo(new BigDecimal("110.00"));
    }

    private TitleResponseDto title(TitleType type, String value) {
        return new TitleResponseDto(
                UUID.randomUUID(),
                "Title",
                type,
                List.of(),
                new BigDecimal(value),
                LocalDateTime.now(),
                null,
                endDate,
                null,
                null
        );
    }
}
