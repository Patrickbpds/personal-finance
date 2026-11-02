package com.patrick.personalfinance.security.service;

import com.patrick.personalfinance.domain.entity.User;
import com.patrick.personalfinance.domain.exceptions.UnauthenticatedException;
import com.patrick.personalfinance.security.principal.AuthenticatedUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SecurityContextCurrentUserProviderTest {

    private final SecurityContextCurrentUserProvider provider = new SecurityContextCurrentUserProvider();

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void returnsTheUserStoredAsTheAuthenticationPrincipal() {
        User user = user();
        authenticate(user);

        assertThat(provider.getCurrentUser()).isSameAs(user);
    }

    @Test
    void rejectsARequestWithoutAuthentication() {
        assertThatThrownBy(provider::getCurrentUser)
                .isInstanceOf(UnauthenticatedException.class)
                .hasMessageContaining("No authenticated user in the current context");
    }

    @Test
    void rejectsAPrincipalThatIsNotAUser() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "someone@example.com", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThatThrownBy(provider::getCurrentUser)
                .isInstanceOf(UnauthenticatedException.class);
    }

    private void authenticate(User user) {
        AuthenticatedUser principal = new AuthenticatedUser(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private User user() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Test User");
        user.setEmail("provider@example.com");
        user.setPassword("encoded-password");
        return user;
    }
}
