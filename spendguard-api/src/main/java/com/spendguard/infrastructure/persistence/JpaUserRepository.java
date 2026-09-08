package com.spendguard.infrastructure.persistence;

import com.spendguard.application.port.UserRepository;
import com.spendguard.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserRepository extends UserRepository, JpaRepository<User, Long> {
}