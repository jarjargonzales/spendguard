package com.spendguard.infrastructure.persistence;

import com.spendguard.application.port.AuditLogRepository;
import com.spendguard.domain.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaAuditLogRepository extends AuditLogRepository, JpaRepository<AuditLog, Long> {

    @Override
    default List<AuditLog> findByRequestId(Long requestId) {
        return findAll().stream()
                .filter(log -> requestId.equals(log.getRequestId()))
                .toList();
    }
}