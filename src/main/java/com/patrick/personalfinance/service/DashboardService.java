package com.patrick.personalfinance.service;

import com.patrick.personalfinance.application.dto.responses.DashboardResponseDto;
import com.patrick.personalfinance.application.dto.responses.TitleResponseDto;
import com.patrick.personalfinance.domain.enums.TypeTitle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final int MONEY_SCALE = 2;

    private final TitleService titleService;

    @Transactional(readOnly = true)
    public DashboardResponseDto getCashFlow(LocalDateTime startDate, LocalDateTime endDate) {
        List<TitleResponseDto> titles = titleService.getByExpirationDateRange(startDate, endDate);

        BigDecimal totalPayable = BigDecimal.ZERO;
        BigDecimal totalReceivable = BigDecimal.ZERO;
        List<TitleResponseDto> payableTitles = new ArrayList<>();
        List<TitleResponseDto> receivableTitles = new ArrayList<>();

        for (TitleResponseDto title : titles) {
            if (title.type() == TypeTitle.PAYABLE) {
                totalPayable = totalPayable.add(title.value());
                payableTitles.add(title);
            } else if (title.type() == TypeTitle.RECEIVABLE) {
                totalReceivable = totalReceivable.add(title.value());
                receivableTitles.add(title);
            }
        }

        BigDecimal balance = totalReceivable.subtract(totalPayable);

        return new DashboardResponseDto(
                totalPayable.setScale(MONEY_SCALE),
                totalReceivable.setScale(MONEY_SCALE),
                balance.setScale(MONEY_SCALE),
                payableTitles,
                receivableTitles
        );
    }
}
