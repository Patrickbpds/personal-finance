package com.patrick.personalfinance.service;

import com.patrick.personalfinance.application.dto.responses.DashboardResponseDto;
import com.patrick.personalfinance.application.dto.responses.TitleResponseDto;
import com.patrick.personalfinance.domain.enums.TypeTitle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TitleService titleService;

    @Transactional(readOnly = true)
    public DashboardResponseDto getCashFlow(LocalDateTime startDate, LocalDateTime endDate) {
        List<TitleResponseDto> titles = titleService.getByExpirationDateRange(startDate, endDate);

        double totalPayable = 0.0;
        double totalReceivable = 0.0;
        List<TitleResponseDto> payableTitles = new ArrayList<>();
        List<TitleResponseDto> receivableTitles = new ArrayList<>();

        for (TitleResponseDto title : titles) {
            if (title.type() == TypeTitle.PAYABLE) {
                totalPayable += title.value();
                payableTitles.add(title);
            } else if (title.type() == TypeTitle.RECEIVABLE) {
                totalReceivable += title.value();
                receivableTitles.add(title);
            }
        }

        double balance = totalReceivable - totalPayable;

        return new DashboardResponseDto(
                totalPayable,
                totalReceivable,
                balance,
                payableTitles,
                receivableTitles
        );
    }
}
