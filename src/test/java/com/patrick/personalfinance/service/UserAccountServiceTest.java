package com.patrick.personalfinance.service;

import com.patrick.personalfinance.application.dto.requests.UpdateProfileRequest;
import com.patrick.personalfinance.application.dto.responses.UserResponseDto;
import com.patrick.personalfinance.application.port.CurrentUserProvider;
import com.patrick.personalfinance.domain.entity.User;
import com.patrick.personalfinance.domain.exceptions.EmailAlreadyExistsException;
import com.patrick.personalfinance.domain.exceptions.InvalidCredentialsException;
import com.patrick.personalfinance.domain.repository.UserRepository;
import com.patrick.personalfinance.security.dto.request.CreateUserRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private UserAccountService userAccountService;

    @Test
    void registersAUserWithAnEncodedPassword() {
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-secret");

        userAccountService.register(
                new CreateUserRequest("New User", "new@example.com", "secret123", null));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("new@example.com");
        assertThat(captor.getValue().getPassword()).isEqualTo("encoded-secret");
    }

    @Test
    void rejectsAnEmailThatIsAlreadyRegistered() {
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userAccountService.register(
                new CreateUserRequest("Taken User", "taken@example.com", "secret123", null)))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage("Email already registered");

        verify(userRepository, never()).saveAndFlush(any(User.class));
    }

    @Test
    void translatesAConcurrentUniqueViolationIntoTheSameConflict() {
        when(userRepository.existsByEmail("race@example.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-secret");
        when(userRepository.saveAndFlush(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key"));

        assertThatThrownBy(() -> userAccountService.register(
                new CreateUserRequest("Race User", "race@example.com", "secret123", null)))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage("Email already registered");
    }

    @Test
    void returnsTheAuthenticatedProfile() {
        User user = user();
        when(currentUserProvider.getCurrentUser()).thenReturn(user);

        UserResponseDto result = userAccountService.getCurrentUser();

        assertThat(result.email()).isEqualTo(user.getEmail());
    }

    @Test
    void updatesTheProfileWithoutTouchingThePasswordWhenNoneIsGiven() {
        User user = user();
        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        UserResponseDto result = userAccountService.updateCurrentUser(
                new UpdateProfileRequest("Updated Name", "photo.png", null, null));

        assertThat(result.name()).isEqualTo("Updated Name");
        assertThat(user.getPassword()).isEqualTo("encoded-current");
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void rejectsAPasswordChangeWithTheWrongCurrentPassword() {
        User user = user();
        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(passwordEncoder.matches("wrong", "encoded-current")).thenReturn(false);

        assertThatThrownBy(() -> userAccountService.updateCurrentUser(
                new UpdateProfileRequest("Updated Name", null, "wrong", "brand-new")))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Current password is incorrect");

        verify(userRepository, never()).save(any(User.class));
        assertThat(user.getName()).isEqualTo("Original Name");
    }

    @Test
    void replacesThePasswordWhenTheCurrentPasswordMatches() {
        User user = user();
        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(passwordEncoder.matches("secret123", "encoded-current")).thenReturn(true);
        when(passwordEncoder.encode("brand-new")).thenReturn("encoded-new");
        when(userRepository.save(user)).thenReturn(user);

        userAccountService.updateCurrentUser(
                new UpdateProfileRequest("Updated Name", null, "secret123", "brand-new"));

        assertThat(user.getPassword()).isEqualTo("encoded-new");
    }

    @Test
    void deactivatesTheAuthenticatedProfile() {
        User user = user();
        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        userAccountService.deactivateCurrentUser();

        assertThat(user.isActive()).isFalse();
        assertThat(user.getInactivationDate()).isNotNull();
        verify(userRepository).save(user);
    }

    private User user() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Original Name");
        user.setEmail("profile@example.com");
        user.setPassword("encoded-current");
        user.setRegistrationDate(LocalDateTime.now().minusYears(1));
        return user;
    }
}
