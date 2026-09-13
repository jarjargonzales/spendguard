package com.spendguard.unit;

import com.spendguard.application.port.ExpenseRequestRepository;
import com.spendguard.application.port.PolicyRepository;
import com.spendguard.application.port.UserRepository;
import com.spendguard.application.service.PolicyEngineService;
import com.spendguard.domain.entity.*;
import com.spendguard.domain.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PolicyEngineServiceTest {

    @Mock private PolicyRepository policyRepository;
    @Mock private UserRepository userRepository;
    @Mock private ExpenseRequestRepository requestRepository;

    @InjectMocks private PolicyEngineService service;

    private ExpenseRequest request;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        Department dept = new Department();
        dept.setDepartmentId(10L);

        ExpenseCategory cat = new ExpenseCategory();
        cat.setCategoryId(20L);

        request = new ExpenseRequest();
        request.setAmount(new BigDecimal("800"));
        request.setDepartment(dept);
        request.setCategory(cat);
    }

    @Test
    void evaluateAndAssignApprovers_shouldThrowWhenNoPolicy() {
        when(policyRepository.findByActiveTrue()).thenReturn(List.of());

        assertThrows(BusinessException.class, () -> service.evaluateAndAssignApprovers(request));
    }

    @Test
    void evaluateAndAssignApprovers_shouldSetUnderReviewWhenPolicyExists() {
        ExpensePolicy policy = new ExpensePolicy();
        policy.setPriority(1);
        policy.setMinAmount(BigDecimal.ZERO);
        policy.setMaxAmount(new BigDecimal("1000"));
        policy.setApproverRoles("MANAGER");
        policy.setApprovalChainType("SEQUENTIAL");

        when(policyRepository.findByActiveTrue()).thenReturn(List.of(policy));
        when(requestRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.evaluateAndAssignApprovers(request);

        assertEquals("UNDER_REVIEW", request.getStatus().name());
    }
}