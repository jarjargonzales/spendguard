package com.spendguard.application.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class BudgetStatusDTO {

    private Long departmentId;
    private String departmentName;
    private String monthYear;
    private BigDecimal budgetAllocated;
    private BigDecimal budgetConsumed;
    private BigDecimal remainingBudget;
    private double consumptionPercentage;
}