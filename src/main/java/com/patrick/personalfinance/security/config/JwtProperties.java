package com.patrick.personalfinance.security.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(

        @NotBlank(message = "JWT_SECRET must be set; generate one with: openssl rand -base64 48")
        @Size(min = 32, message = "JWT_SECRET must be at least 32 characters long")
        String secret,

        @NotNull(message = "jwt.expiration must be set")
        Duration expiration,

        @NotBlank(message = "jwt.issuer must be set")
        String issuer
) {

    public JwtProperties {
        if (expiration != null && expiration.compareTo(Duration.ofSeconds(1)) < 0) {
            throw new IllegalArgumentException("jwt.expiration must be at least 1 second");
        }
    }
}
