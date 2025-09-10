package com.patrick.personalfinance.security.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class TokenResponse {
    @JsonProperty("access_token")
    private final String token;

    @JsonProperty("token_type")
    private final String tokenType = "Bearer";

    @JsonProperty("expires_in")
    private final long expiresIn = 86400;

    public TokenResponse(String token) {
        this.token = token;
    }
}
