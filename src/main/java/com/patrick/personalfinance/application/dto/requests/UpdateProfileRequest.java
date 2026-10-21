package com.patrick.personalfinance.application.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(

        @NotBlank(message = "Name cannot be empty")
        @Size(min = 3, max = 80, message = "Name must be between 3 and 80 characters")
        String name,

        String photo,

        String currentPassword,

        @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
        String newPassword
) {
}
