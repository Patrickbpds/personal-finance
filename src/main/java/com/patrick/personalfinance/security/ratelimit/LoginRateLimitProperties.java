package com.patrick.personalfinance.security.ratelimit;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "app.login-rate-limit")
public record LoginRateLimitProperties(
        boolean enabled,

        @Min(value = 1, message = "app.login-rate-limit.max-attempts must be at least 1")
        int maxAttempts,

        @NotNull(message = "app.login-rate-limit.window must be set")
        Duration window
) {

    public LoginRateLimitProperties {
        if (window != null && window.compareTo(Duration.ofSeconds(1)) < 0) {
            throw new IllegalArgumentException("app.login-rate-limit.window must be at least 1 second");
        }
    }
}
