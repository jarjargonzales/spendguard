package com.spendguard.application.port;

import com.spendguard.domain.entity.ExpenseRequest;
import com.spendguard.domain.enums.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface ExpenseRequestRepository {
    ExpenseRequest save(ExpenseRequest request);
    Optional<ExpenseRequest> findById(Long id);
    List<ExpenseRequest> findByRequester_UserId(Long requesterId);
    List<ExpenseRequest> findByStatus(RequestStatus status);
    List<ExpenseRequest> findByDepartment_DepartmentId(Long departmentId);
    void delete(ExpenseRequest request);
}