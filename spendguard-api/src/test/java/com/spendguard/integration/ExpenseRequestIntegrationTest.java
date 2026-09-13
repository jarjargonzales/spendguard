package com.spendguard.integration;

import com.spendguard.SpendGuardApplication;
import com.spendguard.application.port.DepartmentRepository;
import com.spendguard.application.port.UserRepository;
import com.spendguard.domain.entity.Department;
import com.spendguard.domain.entity.User;
import com.spendguard.domain.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SpendGuardApplication.class)
@ActiveProfiles("test")
class ExpenseRequestIntegrationTest {

    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private UserRepository userRepository;

    @Test
    void shouldPersistDepartmentAndUser() {
        Department dept = new Department();
        dept.setName("Ingeniería");
        dept.setMonthlyBudget(new BigDecimal("10000"));
        dept.setActive(true);
        dept.setVersion("1");
        dept.setCreated(new Timestamp(System.currentTimeMillis()));
        dept.setCreatedBy(1L);
        dept.setUpdated(new Timestamp(System.currentTimeMillis()));
        dept.setUpdatedBy(1L);
        dept.setOwnerId(1L);

        Department saved = departmentRepository.save(dept);
        assertNotNull(saved.getDepartmentId());

        User user = new User();
        user.setUsername("juan");
        user.setEmail("juan@test.com");
        user.setPasswordHash("hash");
        user.setRole(UserRole.EMPLOYEE);
        user.setDepartment(saved);
        user.setActive(true);
        user.setVersion("1");
        user.setCreated(new Timestamp(System.currentTimeMillis()));
        user.setCreatedBy(1L);
        user.setUpdated(new Timestamp(System.currentTimeMillis()));
        user.setUpdatedBy(1L);
        user.setOwnerId(1L);

        User savedUser = userRepository.save(user);
        assertNotNull(savedUser.getUserId());
        assertEquals("juan", savedUser.getUsername());
    }
}