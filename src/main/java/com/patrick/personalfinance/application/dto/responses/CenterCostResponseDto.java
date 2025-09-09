package com.patrick.personalfinance.application.dto.responses;

import java.util.UUID;

public record CenterCostResponseDto (

        UUID id,
        String description,
        String observation
        ) {
}
