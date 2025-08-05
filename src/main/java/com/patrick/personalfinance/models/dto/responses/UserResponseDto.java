package com.patrick.personalfinance.models.dto.responses;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDto (

        UUID id,
        String name,
        String email,
        String photo,
        LocalDateTime inactivationDate

){

}
