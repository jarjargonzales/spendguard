package com.spendguard.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "expense_policies")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ExpensePolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_expense_policies")
    @SequenceGenerator(name = "seq_expense_policies", sequenceName = "seq_expense_policies", allocationSize = 1)
    @Column(name = "policy_id")
    private Long policyId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "min_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal minAmount = BigDecimal.ZERO;

    @Column(name = "max_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal maxAmount = new BigDecimal("999999999");

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ExpenseCategory category;

    @Column(name = "requires_approval", nullable = false)
    private Boolean requiresApproval = true;

    @Column(name = "approval_chain_type", nullable = false, length = 20)
    private String approvalChainType = "SEQUENTIAL";

    @Column(name = "approver_roles", nullable = false, length = 500)
    private String approverRoles;

    @Column(nullable = false)
    private Integer priority = 100;

    @Column(nullable = false)
    private Boolean active = true;

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