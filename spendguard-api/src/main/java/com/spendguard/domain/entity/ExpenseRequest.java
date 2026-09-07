package com.spendguard.domain.entity;

import com.spendguard.domain.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "expense_requests")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ExpenseRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_expense_requests")
    @SequenceGenerator(name = "seq_expense_requests", sequenceName = "seq_expense_requests", allocationSize = 1)
    @Column(name = "request_id")
    private Long requestId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requester_id")
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id")
    private ExpenseCategory category;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency = "USD";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RequestStatus status;

    @Column(name = "submission_date")
    private Timestamp submissionDate;

    @Column(name = "resolution_date")
    private Timestamp resolutionDate;

    @Version
    @Column(name = "version_opt", nullable = false)
    private Integer versionOpt = 0;

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