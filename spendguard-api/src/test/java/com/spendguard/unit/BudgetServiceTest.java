package com.spendguard.unit;

import com.spendguard.application.port.BudgetConsumptionRepository;
import com.spendguard.application.port.DepartmentRepository;
import com.spendguard.application.service.BudgetService;
import com.spendguard.domain.entity.*;
import com.spendguard.domain.exception.InsufficientBudgetException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BudgetServiceTest {

    @Mock private BudgetConsumptionRepository budgetRepository;
    @Mock private DepartmentRepository departmentRepository;

    @InjectMocks private BudgetService service;

    private Department department;
    private User requester;
    private ExpenseRequest request;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        department = new Department();
        department.setDepartmentId(10L);
        department.setMonthlyBudget(new BigDecimal("1000"));

        requester = new User();
        requester.setUserId(1L);

        request = new ExpenseRequest();
        request.setAmount(new BigDecimal("300"));
        request.setDepartment(department);
        request.setRequester(requester);
    }

    @Test
    void consumeBudget_shouldAddAmountToConsumption() {
        BudgetConsumption existing = new BudgetConsumption();
        existing.setBudgetAllocated(new BigDecimal("1000"));
        existing.setBudgetConsumed(new BigDecimal("100"));

        when(budgetRepository.findByDepartment_DepartmentIdAndMonthYear(any(), any()))
                .thenReturn(Optional.of(existing));
        when(budgetRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.consumeBudget(request);

        assertEquals(new BigDecimal("400"), existing.getBudgetConsumed());
    }

    @Test
    void consumeBudget_shouldThrowWhenExceeds() {
        BudgetConsumption existing = new BudgetConsumption();
        existing.setBudgetAllocated(new BigDecimal("200"));
        existing.setBudgetConsumed(new BigDecimal("100"));

        when(budgetRepository.findByDepartment_DepartmentIdAndMonthYear(any(), any()))
                .thenReturn(Optional.of(existing));

        assertThrows(InsufficientBudgetException.class, () -> service.consumeBudget(request));
    }
}