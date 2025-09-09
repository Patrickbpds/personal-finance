package com.patrick.personalfinance.application.dto.requests;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

public record CenterCostRequestDto(

        @NotBlank(message = "Description cannot be empty")
        @Size(min = 3, max = 100, message = "Description must be between 3 and 100 characters")
        String description,

        @Size(max = 500, message = "Observation cannot exceed 500 characters")
        String observation
) {
}
