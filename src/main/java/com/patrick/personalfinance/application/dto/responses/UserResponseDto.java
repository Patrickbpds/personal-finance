package com.patrick.personalfinance.application.dto.responses;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDto (

        UUID id,
        String name,
        String email,
        String photo,
        LocalDateTime registrationDate,
        LocalDateTime inactivationDate,
        LocalDateTime updatedAt
){

}
