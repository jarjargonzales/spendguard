package com.spendguard.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "audit_log")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_audit_log")
    @SequenceGenerator(name = "seq_audit_log", sequenceName = "seq_audit_log", allocationSize = 1)
    @Column(name = "audit_id")
    private Long auditId;

    @Column(name = "request_id")
    private Long requestId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(name = "old_status", length = 20)
    private String oldStatus;

    @Column(name = "new_status", length = 20)
    private String newStatus;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "text")
    private String userAgent;

    @Column(name = "event_timestamp", nullable = false)
    private Timestamp eventTimestamp;

    @Column(columnDefinition = "text")
    private String details;

    @Column(length = 20, nullable = false)
    private String version = "1";

    @Column(nullable = false)
    private Timestamp created;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(nullable = false)
    private Timestamp updated;

    @Column(name = "updated_by", nullable = false)
    private Long updatedBy;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;
}