package com.spendguard.application.port;

import com.spendguard.domain.entity.AuditLog;

public interface AuditLogRepository {
    AuditLog save(AuditLog log);
}