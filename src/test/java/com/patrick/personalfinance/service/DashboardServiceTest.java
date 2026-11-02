package com.patrick.personalfinance.service;

import com.patrick.personalfinance.application.dto.responses.DashboardResponseDto;
import com.patrick.personalfinance.application.dto.responses.TitleResponseDto;
import com.patrick.personalfinance.application.port.CurrentUserProvider;
import com.patrick.personalfinance.domain.entity.Title;
import com.patrick.personalfinance.domain.entity.User;
import com.patrick.personalfinance.domain.enums.PaymentStatus;
import com.patrick.personalfinance.domain.enums.TitleType;
import com.patrick.personalfinance.domain.exceptions.ResourceBadRequestException;
import com.patrick.personalfinance.domain.repository.TitleRepository;
import com.patrick.personalfinance.domain.repository.TitleRepository.TitleTypeTotal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private TitleRepository titleRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private DashboardService dashboardService;

    private final LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 0, 0);
    private final LocalDateTime endDate = LocalDateTime.of(2026, 12, 31, 23, 59);

    @Test
    void usesRepositoryAggregatesForTotals() {
        User user = user();
        List<TitleTypeTotal> typeTotals = List.of(
                typeTotal(TitleType.RECEIVABLE, "0.30"),
                typeTotal(TitleType.PAYABLE, "0.10")
        );

        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(titleRepository.findByUserAndExpirationDateBetween(user, startDate, endDate)).thenReturn(List.of());
        when(titleRepository.sumValueByType(user, startDate, endDate)).thenReturn(typeTotals);
        when(titleRepository.sumPaidValue(user, startDate, endDate)).thenReturn(new BigDecimal("0.10"));
        when(titleRepository.sumOverdueValue(eq(user), eq(startDate), eq(endDate), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("0.20"));
        when(titleRepository.sumPendingValue(eq(user), eq(startDate), eq(endDate), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("0.10"));

        DashboardResponseDto result = dashboardService.getCashFlow(startDate, endDate);

        assertThat(result.totalReceivable()).isEqualTo(new BigDecimal("0.30"));
        assertThat(result.totalPayable()).isEqualTo(new BigDecimal("0.10"));
        assertThat(result.balance()).isEqualTo(new BigDecimal("0.20"));
        assertThat(result.totalPaid()).isEqualTo(new BigDecimal("0.10"));
        assertThat(result.totalOverdue()).isEqualTo(new BigDecimal("0.20"));
        assertThat(result.totalPending()).isEqualTo(new BigDecimal("0.10"));
    }

    @Test
    void returnsZeroTotalsScaledToTwoDecimalsWhenThereAreNoTitles() {
        User user = user();

        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(titleRepository.findByUserAndExpirationDateBetween(user, startDate, endDate)).thenReturn(List.of());
        when(titleRepository.sumValueByType(user, startDate, endDate)).thenReturn(List.of());
        when(titleRepository.sumPaidValue(user, startDate, endDate)).thenReturn(null);
        when(titleRepository.sumOverdueValue(eq(user), eq(startDate), eq(endDate), any(LocalDateTime.class)))
                .thenReturn(null);
        when(titleRepository.sumPendingValue(eq(user), eq(startDate), eq(endDate), any(LocalDateTime.class)))
                .thenReturn(null);

        DashboardResponseDto result = dashboardService.getCashFlow(startDate, endDate);

        assertThat(result.totalReceivable()).isEqualTo(new BigDecimal("0.00"));
        assertThat(result.totalPayable()).isEqualTo(new BigDecimal("0.00"));
        assertThat(result.balance()).isEqualTo(new BigDecimal("0.00"));
        assertThat(result.totalPaid()).isEqualTo(new BigDecimal("0.00"));
        assertThat(result.totalOverdue()).isEqualTo(new BigDecimal("0.00"));
        assertThat(result.totalPending()).isEqualTo(new BigDecimal("0.00"));
    }

    @Test
    void splitsTitlesByTypeAndReportsEachPaymentStatus() {
        User user = user();
        Title paid = title(TitleType.RECEIVABLE, "150.00");
        paid.setPaymentDate(LocalDateTime.now().minusDays(1));
        Title overdue = title(TitleType.PAYABLE, "40.00");
        overdue.setExpirationDate(LocalDateTime.now().minusDays(10));
        Title pending = title(TitleType.RECEIVABLE, "60.00");
        pending.setExpirationDate(LocalDateTime.now().plusDays(10));

        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(titleRepository.findByUserAndExpirationDateBetween(user, startDate, endDate))
                .thenReturn(List.of(paid, overdue, pending));
        when(titleRepository.sumValueByType(user, startDate, endDate)).thenReturn(List.of());
        when(titleRepository.sumPaidValue(user, startDate, endDate)).thenReturn(null);
        when(titleRepository.sumOverdueValue(eq(user), eq(startDate), eq(endDate), any(LocalDateTime.class)))
                .thenReturn(null);
        when(titleRepository.sumPendingValue(eq(user), eq(startDate), eq(endDate), any(LocalDateTime.class)))
                .thenReturn(null);

        DashboardResponseDto result = dashboardService.getCashFlow(startDate, endDate);

        assertThat(result.receivableTitles()).hasSize(2);
        assertThat(result.receivableTitles())
                .extracting(TitleResponseDto::paymentStatus)
                .containsExactly(PaymentStatus.PAID, PaymentStatus.PENDING);
        assertThat(result.payableTitles())
                .extracting(TitleResponseDto::paymentStatus)
                .containsExactly(PaymentStatus.OVERDUE);
    }

    @Test
    void rejectsInvertedDateRange() {
        LocalDateTime later = LocalDateTime.of(2026, 12, 31, 0, 0);
        LocalDateTime earlier = LocalDateTime.of(2026, 1, 1, 0, 0);

        assertThatThrownBy(() -> dashboardService.getCashFlow(later, earlier))
                .isInstanceOf(ResourceBadRequestException.class)
                .hasMessageContaining("startDate must not be after endDate");

        verifyNoInteractions(titleRepository, currentUserProvider);
    }

    private TitleTypeTotal typeTotal(TitleType type, String total) {
        TitleTypeTotal projection = mock(TitleTypeTotal.class);
        when(projection.getType()).thenReturn(type);
        when(projection.getTotal()).thenReturn(new BigDecimal(total));
        return projection;
    }

    private Title title(TitleType type, String value) {
        Title title = new Title();
        title.setId(UUID.randomUUID());
        title.setDescription("Title");
        title.setType(type);
        title.setValue(new BigDecimal(value));
        title.setExpirationDate(endDate);
        title.setRegistrationDate(LocalDateTime.now());
        return title;
    }

    private User user() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Test User");
        user.setEmail("dashboard@example.com");
        user.setPassword("encoded-password");
        return user;
    }
}
