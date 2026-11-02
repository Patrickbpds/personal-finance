package com.patrick.personalfinance.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.patrick.personalfinance.security.config.JwtProperties;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET = "0123456789abcdef0123456789abcdef";
    private static final String ISSUER = "personal-finance-app";

    private final JwtService jwtService = new JwtService(
            new JwtProperties(SECRET, Duration.ofHours(24), ISSUER)
    );

    @Test
    void issuesTokenWithSubjectIssuerAndExpiry() {
        IssuedToken issued = jwtService.generateToken("user@example.com");

        DecodedJWT decoded = jwtService.verify(issued.value());

        assertThat(decoded.getSubject()).isEqualTo("user@example.com");
        assertThat(decoded.getIssuer()).isEqualTo(ISSUER);
        assertThat(decoded.getExpiresAt()).isAfter(new Date());
    }

    @Test
    void reportsExpirationInSeconds() {
        IssuedToken issued = jwtService.generateToken("user@example.com");

        assertThat(issued.expiresInSeconds()).isEqualTo(Duration.ofHours(24).toSeconds());
    }

    @Test
    void rejectsTokenSignedWithAnotherSecret() {
        JwtService anotherService = new JwtService(
                new JwtProperties("fedcba9876543210fedcba9876543210", Duration.ofHours(1), ISSUER)
        );
        String tokenFromAnotherSecret = anotherService.generateToken("user@example.com").value();

        assertThatThrownBy(() -> jwtService.verify(tokenFromAnotherSecret))
                .isInstanceOf(SignatureVerificationException.class);
    }

    @Test
    void rejectsExpiredToken() {
        String expiredToken = JWT.create()
                .withSubject("user@example.com")
                .withIssuedAt(Date.from(Instant.now().minus(Duration.ofHours(2))))
                .withExpiresAt(Date.from(Instant.now().minus(Duration.ofHours(1))))
                .withIssuer(ISSUER)
                .sign(Algorithm.HMAC256(SECRET));

        assertThatThrownBy(() -> jwtService.verify(expiredToken))
                .isInstanceOf(TokenExpiredException.class);
    }

    @Test
    void rejectsTokenFromAnotherIssuer() {
        String tokenFromAnotherIssuer = JWT.create()
                .withSubject("user@example.com")
                .withExpiresAt(Date.from(Instant.now().plusSeconds(60)))
                .withIssuer("another-app")
                .sign(Algorithm.HMAC256(SECRET));

        assertThatThrownBy(() -> jwtService.verify(tokenFromAnotherIssuer))
                .isInstanceOf(InvalidClaimException.class);
    }

    @Test
    void matchesTokenSubjectAgainstUserDetails() {
        DecodedJWT decoded = jwtService.verify(jwtService.generateToken("user@example.com").value());

        assertThat(jwtService.isTokenValid(decoded, userDetails("user@example.com"))).isTrue();
        assertThat(jwtService.isTokenValid(decoded, userDetails("other@example.com"))).isFalse();
    }

    private UserDetails userDetails(String email) {
        return org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password("secret123")
                .authorities(List.of())
                .build();
    }
}
