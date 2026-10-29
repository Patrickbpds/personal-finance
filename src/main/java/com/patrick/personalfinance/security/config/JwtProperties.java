package com.patrick.personalfinance.security.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(

        @NotBlank(message = "JWT_SECRET must be set; generate one with: openssl rand -base64 48")
        @Size(min = 32, message = "JWT_SECRET must be at least 32 characters long")
        String secret
) {
}
