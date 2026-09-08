package com.spendguard.infrastructure.persistence;

import com.spendguard.application.port.ExpenseRequestRepository;
import com.spendguard.domain.entity.ExpenseRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaExpenseRequestRepository extends ExpenseRequestRepository, JpaRepository<ExpenseRequest, Long> {
}