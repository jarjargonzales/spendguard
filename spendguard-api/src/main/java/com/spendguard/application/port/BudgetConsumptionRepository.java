package com.spendguard.application.port;

import com.spendguard.domain.entity.BudgetConsumption;

import java.util.Optional;

public interface BudgetConsumptionRepository {
    Optional<BudgetConsumption> findByDepartment_DepartmentIdAndMonthYear(Long departmentId, String monthYear);
    BudgetConsumption save(BudgetConsumption consumption);
}