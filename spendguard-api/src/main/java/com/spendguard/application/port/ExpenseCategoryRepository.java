package com.spendguard.application.port;

import com.spendguard.domain.entity.ExpenseCategory;

import java.util.Optional;

public interface ExpenseCategoryRepository {
    Optional<ExpenseCategory> findById(Long id);
    ExpenseCategory save(ExpenseCategory category);
}
