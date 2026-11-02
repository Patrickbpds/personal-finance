package com.patrick.personalfinance.application.dto.requests;

import com.patrick.personalfinance.domain.enums.TitleType;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TitleRequestDto(

        @NotBlank(message = "Description cannot be empty")
        @Size(min = 3, max = 100, message = "Description must be between 3 and 100 characters")
        String description,

        @NotNull(message = "Type cannot be null")
        TitleType type,

        List<UUID> centerCostIds,

        @NotNull(message = "Value cannot be null")
        @Positive(message = "Value must be positive")
        @Digits(integer = 13, fraction = 2, message = "Value must have at most 13 integer digits and 2 decimal places")
        BigDecimal value,

        LocalDateTime registrationDate,

        LocalDateTime referenceDate,

        @NotNull(message = "Expiration date cannot be null")
        LocalDateTime expirationDate,

        LocalDateTime paymentDate,

        @Size(max = 500, message = "Observation cannot exceed 500 characters")
        String observation
) {
}
