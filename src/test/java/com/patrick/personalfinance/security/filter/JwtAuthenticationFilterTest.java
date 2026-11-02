package com.patrick.personalfinance.security.filter;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.patrick.personalfinance.security.service.JpaUserDetailsService;
import com.patrick.personalfinance.security.service.JwtService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    private final JwtService jwtService = mock(JwtService.class);
    private final JpaUserDetailsService userDetailsService = mock(JpaUserDetailsService.class);
    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userDetailsService);
    private final FilterChain filterChain = mock(FilterChain.class);

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void ignoresRequestsWithoutBearerToken() throws Exception {
        filter.doFilter(requestWithoutAuthorization(), new MockHttpServletResponse(), filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtService, never()).verify(any());
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void verifiesTokenOnceAndAuthenticatesUser() throws Exception {
        DecodedJWT decodedJWT = decodedJWTWithSubject("user@example.com");
        UserDetails userDetails = userDetails("user@example.com");
        when(jwtService.verify("valid-token")).thenReturn(decodedJWT);
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid(decodedJWT, userDetails)).thenReturn(true);

        filter.doFilter(requestWithBearerToken("valid-token"), new MockHttpServletResponse(), filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isEqualTo(userDetails);
        verify(jwtService, times(1)).verify("valid-token");
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void keepsRequestAnonymousWhenTokenIsExpired() throws Exception {
        when(jwtService.verify("expired-token"))
                .thenThrow(new TokenExpiredException("expired", Instant.now()));

        filter.doFilter(requestWithBearerToken("expired-token"), new MockHttpServletResponse(), filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(userDetailsService, never()).loadUserByUsername(any());
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void keepsRequestAnonymousWhenTokenIsInvalid() throws Exception {
        when(jwtService.verify("tampered-token"))
                .thenThrow(new SignatureVerificationException(null));

        filter.doFilter(requestWithBearerToken("tampered-token"), new MockHttpServletResponse(), filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(userDetailsService, never()).loadUserByUsername(any());
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void keepsRequestAnonymousWhenTokenHasNoSubject() throws Exception {
        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        when(decodedJWT.getSubject()).thenReturn(null);
        when(jwtService.verify("subjectless-token")).thenReturn(decodedJWT);

        filter.doFilter(requestWithBearerToken("subjectless-token"), new MockHttpServletResponse(), filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(userDetailsService, never()).loadUserByUsername(any());
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void keepsRequestAnonymousWhenUserNoLongerExists() throws Exception {
        DecodedJWT decodedJWT = decodedJWTWithSubject("gone@example.com");
        when(jwtService.verify("token-of-deleted-user")).thenReturn(decodedJWT);
        when(userDetailsService.loadUserByUsername("gone@example.com"))
                .thenThrow(new UsernameNotFoundException("User not found"));

        filter.doFilter(requestWithBearerToken("token-of-deleted-user"), new MockHttpServletResponse(), filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void doesNotVerifyTokenWhenRequestIsAlreadyAuthenticated() throws Exception {
        UserDetails userDetails = userDetails("user@example.com");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );

        filter.doFilter(requestWithBearerToken("valid-token"), new MockHttpServletResponse(), filterChain);

        verify(jwtService, never()).verify(any());
        verify(filterChain).doFilter(any(), any());
    }

    private MockHttpServletRequest requestWithoutAuthorization() {
        return new MockHttpServletRequest("GET", "/api/users/me");
    }

    private MockHttpServletRequest requestWithBearerToken(String token) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users/me");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return request;
    }

    private DecodedJWT decodedJWTWithSubject(String subject) {
        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        when(decodedJWT.getSubject()).thenReturn(subject);
        return decodedJWT;
    }

    private UserDetails userDetails(String email) {
        return org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password("secret123")
                .authorities(List.of())
                .build();
    }
}
