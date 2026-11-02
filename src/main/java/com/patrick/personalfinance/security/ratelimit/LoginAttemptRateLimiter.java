package com.patrick.personalfinance.security.ratelimit;

import com.patrick.personalfinance.domain.exceptions.TooManyRequestsException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@RequiredArgsConstructor
public class LoginAttemptRateLimiter {

    private static final int PURGE_THRESHOLD = 10_000;

    private final LoginRateLimitProperties properties;
    private final ConcurrentMap<String, Attempts> attemptsByKey = new ConcurrentHashMap<>();

    public void checkAllowed(String clientIp, String email) {
        if (!properties.enabled()) {
            return;
        }

        Attempts attempts = attemptsFor(key(clientIp, email));
        attempts.touch();

        if (attempts.bucket().tryConsume(1)) {
            return;
        }

        throw new TooManyRequestsException(
                "Too many login attempts. Try again later",
                secondsToWait(attempts.bucket())
        );
    }

    public void reset(String clientIp, String email) {
        attemptsByKey.remove(key(clientIp, email));
    }

    private Attempts attemptsFor(String key) {
        if (attemptsByKey.size() > PURGE_THRESHOLD) {
            purgeIdle();
        }
        return attemptsByKey.computeIfAbsent(key, ignored -> new Attempts(newBucket()));
    }

    private Bucket newBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(properties.maxAttempts())
                        .refillGreedy(properties.maxAttempts(), properties.window())
                        .build())
                .build();
    }

    private long secondsToWait(Bucket bucket) {
        long nanos = bucket.estimateAbilityToConsume(1).getNanosToWaitForRefill();
        return Math.max(1, Duration.ofNanos(nanos).toSeconds());
    }

    private void purgeIdle() {
        Instant cutoff = Instant.now().minus(properties.window());
        attemptsByKey.entrySet().removeIf(entry -> entry.getValue().lastSeen().isBefore(cutoff));
    }

    private String key(String clientIp, String email) {
        return clientIp + ":" + email.trim().toLowerCase(Locale.ROOT);
    }

    private static final class Attempts {

        private final Bucket bucket;
        private volatile Instant lastSeen = Instant.now();

        private Attempts(Bucket bucket) {
            this.bucket = bucket;
        }

        private Bucket bucket() {
            return bucket;
        }

        private Instant lastSeen() {
            return lastSeen;
        }

        private void touch() {
            lastSeen = Instant.now();
        }
    }
}
