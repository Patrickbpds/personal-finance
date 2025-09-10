package com.patrick.personalfinance.application.dto.requests;

import com.patrick.personalfinance.domain.enums.TypeTitle;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TitleRequestDto(

        String description,

        TypeTitle type,

        List<UUID> centerCostIds,

        Double value,

        LocalDateTime registrationDate,

        LocalDateTime referenceDate,

        LocalDateTime expirationDate,

        LocalDateTime paymentDate,

        String observation
) {
}
