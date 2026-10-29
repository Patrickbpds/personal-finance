package com.patrick.personalfinance.application.dto.responses;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponseDto (
        BigDecimal totalPayable,
        BigDecimal totalReceivable,
        BigDecimal balance,
        List<TitleResponseDto> payableTitles,
        List<TitleResponseDto> receivableTitles
){
}
