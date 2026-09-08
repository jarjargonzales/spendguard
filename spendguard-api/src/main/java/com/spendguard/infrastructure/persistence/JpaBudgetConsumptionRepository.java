package com.spendguard.infrastructure.persistence;

import com.spendguard.application.port.BudgetConsumptionRepository;
import com.spendguard.domain.entity.BudgetConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaBudgetConsumptionRepository extends BudgetConsumptionRepository, JpaRepository<BudgetConsumption, Long> {
}