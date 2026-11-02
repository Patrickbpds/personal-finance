package com.patrick.personalfinance.domain.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void usesIdentityEqualityInsteadOfFieldEquality() {
        UUID id = UUID.randomUUID();
        User user = userWithId(id);
        User other = userWithId(id);

        assertThat(user).isEqualTo(user);
        assertThat(user).isNotEqualTo(other);
    }

    @Test
    void toStringDoesNotExposePassword() {
        User user = userWithId(UUID.randomUUID());
        user.setName("Patrick");
        user.setEmail("patrick@example.com");
        user.setPassword("super-secret-password");

        assertThat(user.toString())
                .contains("patrick@example.com")
                .doesNotContain("super-secret-password")
                .doesNotContain("password=");
    }

    @Test
    void inactivateDisablesAccount() {
        User user = userWithId(UUID.randomUUID());

        user.inactivate();

        assertThat(user.isActive()).isFalse();
        assertThat(user.getInactivationDate()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void newUserIsActive() {
        User user = userWithId(UUID.randomUUID());

        assertThat(user.isActive()).isTrue();
    }

    private User userWithId(UUID id) {
        User user = new User();
        user.setId(id);
        return user;
    }
}
