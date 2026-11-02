package com.patrick.personalfinance.application.dto.responses;

import com.patrick.personalfinance.domain.enums.PaymentStatus;
import com.patrick.personalfinance.domain.enums.TitleType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TitleResponseDto(

        UUID id,

        String description,

        TitleType type,

        PaymentStatus paymentStatus,

        List<CenterCostResponseDto> centerCosts,

        BigDecimal value,

        LocalDateTime registrationDate,

        LocalDateTime referenceDate,

        LocalDateTime expirationDate,

        LocalDateTime paymentDate,

        String observation
) {
}
