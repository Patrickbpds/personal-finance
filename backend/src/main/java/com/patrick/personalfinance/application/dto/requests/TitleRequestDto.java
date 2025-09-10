package com.patrick.personalfinance.application.dto.requests;

import com.patrick.personalfinance.domain.enums.TypeTitle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TitleRequestDto(

        @NotBlank(message = "Description cannot be empty")
        @Size(min = 3, max = 100, message = "Description must be between 3 and 100 characters")
        String description,

        @NotNull(message = "Type cannot be null")
        TypeTitle type,

        List<UUID> centerCostIds,

        @NotNull(message = "Value cannot be null")
        @Positive(message = "Value must be positive")
        Double value,

        LocalDateTime registrationDate,

        LocalDateTime referenceDate,

        @NotNull(message = "Expiration date cannot be null")
        LocalDateTime expirationDate,

        LocalDateTime paymentDate,

        @Size(max = 500, message = "Observation cannot exceed 500 characters")
        String observation
) {
}
