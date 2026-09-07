package com.spendguard.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "budget_consumption")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class BudgetConsumption {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_budget_consumption")
    @SequenceGenerator(name = "seq_budget_consumption", sequenceName = "seq_budget_consumption", allocationSize = 1)
    @Column(name = "consumption_id")
    private Long consumptionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "month_year", nullable = false, length = 7)
    private String monthYear;

    @Column(name = "budget_allocated", nullable = false, precision = 15, scale = 2)
    private BigDecimal budgetAllocated;

    @Column(name = "budget_consumed", nullable = false, precision = 15, scale = 2)
    private BigDecimal budgetConsumed = BigDecimal.ZERO;

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