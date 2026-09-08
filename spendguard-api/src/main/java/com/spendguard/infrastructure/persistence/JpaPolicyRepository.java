package com.spendguard.infrastructure.persistence;

import com.spendguard.application.port.PolicyRepository;
import com.spendguard.domain.entity.ExpensePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaPolicyRepository extends PolicyRepository, JpaRepository<ExpensePolicy, Long> {
}