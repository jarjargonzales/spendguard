package com.spendguard.application.port;

import com.spendguard.domain.entity.Department;

import java.util.Optional;

public interface DepartmentRepository {
    Optional<Department> findById(Long id);
    Department save(Department department);
}