package com.spendguard.application.port;

import com.spendguard.domain.entity.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository {
    Optional<Department> findById(Long id);
    List<Department> findAll();
    Department save(Department department);
    void delete(Department department);
}