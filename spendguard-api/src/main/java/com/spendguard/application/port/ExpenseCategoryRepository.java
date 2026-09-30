package com.spendguard.application.port;

import com.spendguard.domain.entity.ExpenseCategory;

import java.util.List;
import java.util.Optional;

public interface ExpenseCategoryRepository {
    Optional<ExpenseCategory> findById(Long id);
    List<ExpenseCategory> findAll();
    ExpenseCategory save(ExpenseCategory category);
    void delete(ExpenseCategory category);
}
