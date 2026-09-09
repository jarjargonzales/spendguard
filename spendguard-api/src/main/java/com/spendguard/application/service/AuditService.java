package com.spendguard.application.service;

import com.spendguard.application.port.AuditLogRepository;
import com.spendguard.domain.entity.AuditLog;
import com.spendguard.domain.entity.ExpenseRequest;
import com.spendguard.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logEvent(ExpenseRequest request, User user, String eventType, String newStatus) {
        AuditLog log = new AuditLog();
        log.setRequestId(request.getRequestId());
        log.setUserId(user.getUserId());
        log.setEventType(eventType);
        log.setNewStatus(newStatus);
        log.setEventTimestamp(new Timestamp(System.currentTimeMillis()));
        log.setVersion("1");
        log.setCreatedBy(user.getUserId());
        log.setUpdatedBy(user.getUserId());
        log.setOwnerId(user.getUserId());
        auditLogRepository.save(log);
    }
}