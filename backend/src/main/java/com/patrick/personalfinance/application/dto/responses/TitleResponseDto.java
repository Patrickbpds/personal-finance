package com.patrick.personalfinance.application.dto.responses;

import com.patrick.personalfinance.domain.enums.TypeTitle;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TitleResponseDto(

        UUID id,

        String description,

        TypeTitle type,

        List<CenterCostResponseDto> centerCosts,

        Double value,

        LocalDateTime registrationDate,

        LocalDateTime referenceDate,

        LocalDateTime expirationDate,

        LocalDateTime paymentDate,

        String observation
) {
}
