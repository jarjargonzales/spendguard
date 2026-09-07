package com.spendguard.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "expense_categories")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ExpenseCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_expense_categories")
    @SequenceGenerator(name = "seq_expense_categories", sequenceName = "seq_expense_categories", allocationSize = 1)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "requires_approval", nullable = false)
    private Boolean requiresApproval = true;

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