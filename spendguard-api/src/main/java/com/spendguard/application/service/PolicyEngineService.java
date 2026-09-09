package com.spendguard.application.service;

import com.spendguard.application.port.ExpenseRequestRepository;
import com.spendguard.application.port.PolicyRepository;
import com.spendguard.application.port.UserRepository;
import com.spendguard.domain.entity.ExpensePolicy;
import com.spendguard.domain.entity.ExpenseRequest;
import com.spendguard.domain.entity.User;
import com.spendguard.domain.enums.RequestStatus;
import com.spendguard.domain.enums.UserRole;
import com.spendguard.domain.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyEngineService {

    private final PolicyRepository policyRepository;
    private final UserRepository userRepository;
    private final ExpenseRequestRepository requestRepository;

    @Transactional
    public void evaluateAndAssignApprovers(ExpenseRequest request) {
        List<ExpensePolicy> policies = policyRepository.findByActiveTrue().stream()
                .filter(p -> p.getDepartment() == null || p.getDepartment().getDepartmentId().equals(request.getDepartment().getDepartmentId()))
                .filter(p -> p.getCategory() == null || p.getCategory().getCategoryId().equals(request.getCategory().getCategoryId()))
                .filter(p -> p.getMinAmount().compareTo(request.getAmount()) <= 0)
                .filter(p -> p.getMaxAmount().compareTo(request.getAmount()) >= 0)
                .sorted(Comparator.comparingInt(ExpensePolicy::getPriority))
                .toList();

        if (policies.isEmpty()) {
            throw new BusinessException("No existe política de aprobación para esta solicitud");
        }

        ExpensePolicy selected = policies.get(0);
        request.setStatus(RequestStatus.UNDER_REVIEW);
        requestRepository.save(request);

        assignApproversByRole(request, selected.getApproverRoles(), selected.getApprovalChainType());
    }

    private void assignApproversByRole(ExpenseRequest request, String approverRoles, String chainType) {
    }
}