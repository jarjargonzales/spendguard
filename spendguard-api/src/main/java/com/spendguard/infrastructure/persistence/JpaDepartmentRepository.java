package com.spendguard.infrastructure.persistence;

import com.spendguard.application.port.DepartmentRepository;
import com.spendguard.domain.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaDepartmentRepository extends DepartmentRepository, JpaRepository<Department, Long> {
}