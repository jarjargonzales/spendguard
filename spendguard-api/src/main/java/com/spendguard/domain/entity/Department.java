package com.spendguard.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "departments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_departments")
    @SequenceGenerator(name = "seq_departments", sequenceName = "seq_departments", allocationSize = 1)
    @Column(name = "department_id")
    private Long departmentId;

    @Column(nullable = false, unique = true, length = 200)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "monthly_budget", nullable = false, precision = 15, scale = 2)
    private BigDecimal monthlyBudget = BigDecimal.ZERO;

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