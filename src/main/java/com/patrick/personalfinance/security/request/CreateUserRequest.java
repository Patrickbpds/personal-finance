package com.patrick.personalfinance.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "Name cannot be empty")
        @Size(min = 3, max = 80, message = "Name must be between 3 and 80 characters")
        String name,

        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Invalid email")
        @Size(min = 6, max = 80, message = "Email must be between 6 and 80 characters")
        String email,

        @NotBlank(message = "Password cannot be empty")
        @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
        String password,

        String photo
) {
}
