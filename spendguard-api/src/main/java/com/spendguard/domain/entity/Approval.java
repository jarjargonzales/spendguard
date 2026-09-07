package com.spendguard.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "approvals")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Approval {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_approvals")
    @SequenceGenerator(name = "seq_approvals", sequenceName = "seq_approvals", allocationSize = 1)
    @Column(name = "approval_id")
    private Long approvalId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id")
    private ExpenseRequest request;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "approver_id")
    private User approver;

    @Column(nullable = false, length = 20)
    private String decision; // APPROVED, REJECTED, ESCALATED, PENDING

    @Column(columnDefinition = "text")
    private String comments;

    @Column(name = "decided_at")
    private Timestamp decidedAt;

    @Column(name = "sequence_order", nullable = false)
    private Integer sequenceOrder;

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