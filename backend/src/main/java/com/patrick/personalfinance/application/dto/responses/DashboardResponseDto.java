package com.patrick.personalfinance.application.dto.responses;

import java.util.List;

public record DashboardResponseDto (
        Double totalPayable,
        Double totalReceivable,
        Double balance,
        List<TitleResponseDto> payableTitles,
        List<TitleResponseDto> receivableTitles
){
}
