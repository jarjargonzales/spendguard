package com.spendguard.application.service;

import com.spendguard.application.port.BudgetConsumptionRepository;
import com.spendguard.application.port.DepartmentRepository;
import com.spendguard.domain.entity.BudgetConsumption;
import com.spendguard.domain.entity.Department;
import com.spendguard.domain.entity.ExpenseRequest;
import com.spendguard.domain.exception.InsufficientBudgetException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetConsumptionRepository budgetRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional
    public void consumeBudget(ExpenseRequest request) {
        Department dept = request.getDepartment();
        String monthYear = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

        BudgetConsumption consumption = budgetRepository
                .findByDepartment_DepartmentIdAndMonthYear(dept.getDepartmentId(), monthYear)
                .orElseGet(() -> {
                    BudgetConsumption newConsumption = new BudgetConsumption();
                    newConsumption.setDepartment(dept);
                    newConsumption.setMonthYear(monthYear);
                    newConsumption.setBudgetAllocated(dept.getMonthlyBudget());
                    newConsumption.setBudgetConsumed(BigDecimal.ZERO);
                    newConsumption.setVersion("1");
                    newConsumption.setCreatedBy(request.getRequester().getUserId());
                    newConsumption.setUpdatedBy(request.getRequester().getUserId());
                    newConsumption.setOwnerId(request.getRequester().getUserId());
                    return newConsumption;
                });

        BigDecimal newConsumed = consumption.getBudgetConsumed().add(request.getAmount());
        if (newConsumed.compareTo(consumption.getBudgetAllocated()) > 0) {
            throw new InsufficientBudgetException("Presupuesto mensual excedido");
        }

        consumption.setBudgetConsumed(newConsumed);
        consumption.setUpdatedBy(request.getRequester().getUserId());
        budgetRepository.save(consumption);
    }

    @Transactional(readOnly = true)
    public double getConsumptionPercentage(Long departmentId, String monthYear) {
        return budgetRepository.findByDepartment_DepartmentIdAndMonthYear(departmentId, monthYear)
                .map(c -> c.getBudgetConsumed().doubleValue() / c.getBudgetAllocated().doubleValue() * 100)
                .orElse(0.0);
    }
}