package com.patrick.personalfinance.security.service;

import com.patrick.personalfinance.application.port.CurrentUserProvider;
import com.patrick.personalfinance.domain.entity.User;
import com.patrick.personalfinance.domain.exceptions.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextCurrentUserProvider implements CurrentUserProvider {

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new ResourceNotFoundException("No authenticated user in the current context");
        }

        return user;
    }
}
