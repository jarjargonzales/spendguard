package com.spendguard.infrastructure.persistence;

import com.spendguard.application.port.ExpenseCategoryRepository;
import com.spendguard.domain.entity.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaExpenseCategoryRepository extends ExpenseCategoryRepository, JpaRepository<ExpenseCategory, Long> {
}