package com.patrick.personalfinance.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.patrick.personalfinance.security.config.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    public IssuedToken generateToken(String email) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(jwtProperties.expiration());
        String token = JWT.create()
                .withSubject(email)
                .withIssuedAt(Date.from(issuedAt))
                .withExpiresAt(Date.from(expiresAt))
                .withIssuer(jwtProperties.issuer())
                .sign(algorithm());
        return new IssuedToken(token, jwtProperties.expiration().toSeconds());
    }

    public DecodedJWT verify(String token) {
        return JWT.require(algorithm())
                .withIssuer(jwtProperties.issuer())
                .build()
                .verify(token);
    }

    public boolean isTokenValid(DecodedJWT decodedJWT, UserDetails userDetails) {
        return userDetails.getUsername().equals(decodedJWT.getSubject());
    }

    private Algorithm algorithm() {
        return Algorithm.HMAC256(jwtProperties.secret());
    }
}
