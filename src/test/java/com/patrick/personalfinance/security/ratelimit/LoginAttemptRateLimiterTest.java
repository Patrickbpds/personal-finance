package com.patrick.personalfinance.security.ratelimit;

import com.patrick.personalfinance.domain.exceptions.TooManyRequestsException;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class LoginAttemptRateLimiterTest {

    private static final String CLIENT_IP = "203.0.113.10";
    private static final String EMAIL = "user@example.com";
    private static final Duration WINDOW = Duration.ofMinutes(15);

    @Test
    void allowsAttemptsUpToTheLimit() {
        LoginAttemptRateLimiter limiter = limiter(3, true);

        for (int attempt = 1; attempt <= 3; attempt++) {
            limiter.checkAllowed(CLIENT_IP, EMAIL);
        }
    }

    @Test
    void rejectsAttemptAboveTheLimit() {
        LoginAttemptRateLimiter limiter = limiter(3, true);
        consumeAll(limiter, 3);

        TooManyRequestsException exception = catchThrowableOfType(
                TooManyRequestsException.class,
                () -> limiter.checkAllowed(CLIENT_IP, EMAIL)
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Too many login attempts. Try again later");
    }

    @Test
    void reportsSecondsToWaitForTheNextAttempt() {
        LoginAttemptRateLimiter limiter = limiter(5, true);
        consumeAll(limiter, 5);

        TooManyRequestsException exception = catchThrowableOfType(
                TooManyRequestsException.class,
                () -> limiter.checkAllowed(CLIENT_IP, EMAIL)
        );

        assertThat(exception.getRetryAfterSeconds()).isBetween(1L, WINDOW.toSeconds() + 1);
    }

    @Test
    void resetAllowsNewAttempts() {
        LoginAttemptRateLimiter limiter = limiter(3, true);
        consumeAll(limiter, 3);

        limiter.reset(CLIENT_IP, EMAIL);

        limiter.checkAllowed(CLIENT_IP, EMAIL);
    }

    @Test
    void keysAreIndependentByIpAndEmail() {
        LoginAttemptRateLimiter limiter = limiter(2, true);
        consumeAll(limiter, 2);

        limiter.checkAllowed(CLIENT_IP, "another@example.com");
        limiter.checkAllowed("198.51.100.7", EMAIL);
    }

    @Test
    void emailIsKeyedCaseInsensitively() {
        LoginAttemptRateLimiter limiter = limiter(2, true);
        limiter.checkAllowed(CLIENT_IP, "User@Example.com");
        limiter.checkAllowed(CLIENT_IP, "user@example.com");

        TooManyRequestsException exception = catchThrowableOfType(
                TooManyRequestsException.class,
                () -> limiter.checkAllowed(CLIENT_IP, "USER@EXAMPLE.COM")
        );

        assertThat(exception).isNotNull();
    }

    @Test
    void allowsUnlimitedAttemptsWhenDisabled() {
        LoginAttemptRateLimiter limiter = limiter(1, false);

        for (int attempt = 1; attempt <= 50; attempt++) {
            limiter.checkAllowed(CLIENT_IP, EMAIL);
        }
    }

    private LoginAttemptRateLimiter limiter(int maxAttempts, boolean enabled) {
        return new LoginAttemptRateLimiter(new LoginRateLimitProperties(enabled, maxAttempts, WINDOW));
    }

    private void consumeAll(LoginAttemptRateLimiter limiter, int attempts) {
        for (int attempt = 1; attempt <= attempts; attempt++) {
            limiter.checkAllowed(CLIENT_IP, EMAIL);
        }
    }
}
