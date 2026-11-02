package com.patrick.personalfinance.service;

import com.patrick.personalfinance.application.dto.responses.DashboardResponseDto;
import com.patrick.personalfinance.application.dto.responses.TitleResponseDto;
import com.patrick.personalfinance.application.mapper.TitleMapper;
import com.patrick.personalfinance.application.port.CurrentUserProvider;
import com.patrick.personalfinance.domain.entity.Title;
import com.patrick.personalfinance.domain.entity.User;
import com.patrick.personalfinance.domain.enums.TitleType;
import com.patrick.personalfinance.domain.exceptions.ResourceBadRequestException;
import com.patrick.personalfinance.domain.repository.TitleRepository;
import com.patrick.personalfinance.domain.repository.TitleRepository.TitleTypeTotal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final int MONEY_SCALE = 2;

    private final TitleRepository titleRepository;
    private final CurrentUserProvider currentUserProvider;

    @Transactional(readOnly = true)
    public DashboardResponseDto getCashFlow(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new ResourceBadRequestException("startDate must not be after endDate");
        }

        User user = currentUserProvider.getCurrentUser();
        LocalDateTime now = LocalDateTime.now();

        List<Title> titles = titleRepository.findByUserAndExpirationDateBetween(user, startDate, endDate);
        List<TitleResponseDto> titlesByType = TitleMapper.toResponseDtoList(titles, now);

        Map<TitleType, BigDecimal> totalsByType = titleRepository
                .sumValueByType(user, startDate, endDate)
                .stream()
                .collect(Collectors.toMap(TitleTypeTotal::getType, TitleTypeTotal::getTotal));

        BigDecimal totalReceivable = money(totalsByType.get(TitleType.RECEIVABLE));
        BigDecimal totalPayable = money(totalsByType.get(TitleType.PAYABLE));

        List<TitleResponseDto> payableTitles = titlesByType.stream()
                .filter(title -> title.type() == TitleType.PAYABLE)
                .toList();

        List<TitleResponseDto> receivableTitles = titlesByType.stream()
                .filter(title -> title.type() == TitleType.RECEIVABLE)
                .toList();

        return new DashboardResponseDto(
                totalPayable,
                totalReceivable,
                totalReceivable.subtract(totalPayable).setScale(MONEY_SCALE),
                money(titleRepository.sumPaidValue(user, startDate, endDate)),
                money(titleRepository.sumOverdueValue(user, startDate, endDate, now)),
                money(titleRepository.sumPendingValue(user, startDate, endDate, now)),
                payableTitles,
                receivableTitles
        );
    }

    private BigDecimal money(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value).setScale(MONEY_SCALE);
    }
}
