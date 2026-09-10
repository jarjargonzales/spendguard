package com.spendguard.application.port;

import java.util.List;
import java.util.Optional;

import com.spendguard.domain.entity.AuditLog;

public interface AuditLogRepository {
    AuditLog save(AuditLog log);
    List<AuditLog> findAll();
    List<AuditLog> findByRequestId(Long requestId);
    Optional<AuditLog> findById(Long id);
}