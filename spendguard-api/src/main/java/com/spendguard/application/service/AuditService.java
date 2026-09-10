package com.spendguard.application.service;

import com.spendguard.application.dto.response.AuditLogEntryDTO;
import com.spendguard.application.port.AuditLogRepository;
import com.spendguard.domain.entity.AuditLog;
import com.spendguard.domain.entity.ExpenseRequest;
import com.spendguard.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

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
    
    @Transactional(readOnly = true)
    public List<AuditLogEntryDTO> getAllLogs() {
        return auditLogRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AuditLogEntryDTO> getLogsByRequestId(Long requestId) {
        return auditLogRepository.findByRequestId(requestId).stream()
                .map(this::toDTO)
                .toList();
    }

    private AuditLogEntryDTO toDTO(AuditLog log) {
        return new AuditLogEntryDTO(
            log.getAuditId(),
            log.getRequestId(),
            log.getUserId(),
            log.getEventType(),
            log.getOldStatus(),
            log.getNewStatus(),
            log.getIpAddress(),
            log.getUserAgent(),
            log.getEventTimestamp(),
            log.getDetails()
        );
    }
}