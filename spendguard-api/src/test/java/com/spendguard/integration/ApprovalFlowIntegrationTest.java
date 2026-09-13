package com.spendguard.integration;

import com.spendguard.SpendGuardApplication;
import com.spendguard.application.port.DepartmentRepository;
import com.spendguard.application.service.BudgetService;
import com.spendguard.domain.entity.Department;
import com.spendguard.domain.entity.ExpenseRequest;
import com.spendguard.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SpendGuardApplication.class)
@ActiveProfiles("test")
class ApprovalFlowIntegrationTest {

    @Autowired private BudgetService budgetService;
    @Autowired private DepartmentRepository departmentRepository;

    @Test
    void consumeBudget_shouldCreateConsumptionRecordWhenNoneExists() {
        Department dept = new Department();
        dept.setName("Finanzas Test");
        dept.setMonthlyBudget(new BigDecimal("5000"));
        dept.setActive(true);
        dept.setVersion("1");
        dept.setCreated(new Timestamp(System.currentTimeMillis()));
        dept.setCreatedBy(1L);
        dept.setUpdated(new Timestamp(System.currentTimeMillis()));
        dept.setUpdatedBy(1L);
        dept.setOwnerId(1L);

        Department saved = departmentRepository.save(dept);

        User requester = new User();
        requester.setUserId(1L);

        ExpenseRequest req = new ExpenseRequest();
        req.setAmount(new BigDecimal("500"));
        req.setDepartment(saved);
        req.setRequester(requester);

        budgetService.consumeBudget(req);
        String monthYear = java.time.YearMonth.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        double pct = budgetService.getConsumptionPercentage(saved.getDepartmentId(), monthYear);
        assertEquals(10.0, pct, 0.1);
    }
}