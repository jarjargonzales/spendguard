package com.spendguard.application.dto.response;

import lombok.*;

import java.sql.Timestamp;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AuditLogEntryDTO {

    private Long auditId;
    private Long requestId;
    private Long userId;
    private String eventType;
    private String oldStatus;
    private String newStatus;
    private String ipAddress;
    private String userAgent;
    private Timestamp eventTimestamp;
    private String details;
}