package com.patrick.personalfinance.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class JwtService {

    @Value("${JWT_SECRET:default-secret-key-change-in-production}")
    private String secret;

    public String generateToken(String email) {
        long jwtExpiration = 86400000;
        try {
            return JWT.create()
                    .withSubject(email)
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpiration))
                    .withIssuer("personal-finance-app")
                    .sign(Algorithm.HMAC256(secret));
        } catch (Exception e) {
            log.error("Error generating JWT token for email: {}", email, e);
            throw new RuntimeException("Could not generate JWT token", e);
        }
    }

    public String extractEmail(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(secret))
                    .withIssuer("personal-finance-app")
                    .build()
                    .verify(token);
            return decodedJWT.getSubject();
        } catch (JWTVerificationException e) {
            log.error("Error extracting email from JWT token", e);
            throw new JWTVerificationException("Invalid JWT token", e);
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractEmail(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (Exception e) {
            log.warn("JWT token validation failed: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(secret))
                    .withIssuer("personal-finance-app")
                    .build()
                    .verify(token);
            return decodedJWT.getExpiresAt().before(new Date());
        } catch (JWTVerificationException e) {
            log.warn("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }
}