package com.spendguard.application.service;

import com.spendguard.application.port.UserRepository;
import com.spendguard.domain.entity.User;
import com.spendguard.domain.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("securityService")
@RequiredArgsConstructor
public class SecurityService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Usuario no autenticado");
        }
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
    }

    public boolean isOwner(Long requestId) {
        return getCurrentUser() != null;
    }

    public boolean isCfoOrAdmin() {
        User user = getCurrentUser();
        return user.getRole() == UserRole.CFO || user.getRole() == UserRole.ADMIN;
    }

    public boolean isManagerOf(Long userId) {
        User user = getCurrentUser();
        return user.getRole() == UserRole.MANAGER || user.getRole() == UserRole.DIRECTOR;
    }
}