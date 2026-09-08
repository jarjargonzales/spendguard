package com.spendguard.application.port;

import com.spendguard.domain.entity.ExpensePolicy;

import java.util.List;

public interface PolicyRepository {
    List<ExpensePolicy> findByActiveTrue();
    List<ExpensePolicy> findByDepartment_DepartmentId(Long departmentId);
    List<ExpensePolicy> findByCategory_CategoryId(Long categoryId);
    ExpensePolicy save(ExpensePolicy policy);
    void delete(ExpensePolicy policy);
}