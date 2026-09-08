package com.spendguard.application.port;

import com.spendguard.domain.entity.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    User save(User user);
}