package com.spendguard.application.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DepartmentResponseDTO {
    private Long departmentId;
    private String name;
    private String description;
    private BigDecimal monthlyBudget;
    private Boolean active;
}