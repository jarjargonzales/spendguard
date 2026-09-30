package com.spendguard.application.service;

import com.spendguard.application.dto.request.DepartmentDTO;
import com.spendguard.application.dto.response.DepartmentResponseDTO;
import com.spendguard.application.port.DepartmentRepository;
import com.spendguard.domain.entity.Department;
import com.spendguard.domain.entity.User;
import com.spendguard.domain.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Transactional(readOnly = true)
    public List<DepartmentResponseDTO> findAll() {
        return departmentRepository.findAll().stream()
                .filter(Department::getActive)
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public DepartmentResponseDTO create(DepartmentDTO dto, User creator) {
        Department dept = new Department();
        dept.setName(dto.getName());
        dept.setDescription(dto.getDescription());
        dept.setMonthlyBudget(dto.getMonthlyBudget());
        dept.setActive(true);
        dept.setVersion("1");
        dept.setCreatedBy(creator.getUserId());
        dept.setUpdatedBy(creator.getUserId());
        dept.setOwnerId(creator.getUserId());
        return toDTO(departmentRepository.save(dept));
    }

    @Transactional
    public DepartmentResponseDTO update(Long id, DepartmentDTO dto, User updater) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Departamento no encontrado"));
        dept.setName(dto.getName());
        dept.setDescription(dto.getDescription());
        dept.setMonthlyBudget(dto.getMonthlyBudget());
        dept.setUpdatedBy(updater.getUserId());
        return toDTO(departmentRepository.save(dept));
    }

    @Transactional
    public void delete(Long id, User deleter) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Departamento no encontrado"));
        dept.setActive(false);
        dept.setUpdatedBy(deleter.getUserId());
        departmentRepository.save(dept);
    }

    private DepartmentResponseDTO toDTO(Department d) {
        return new DepartmentResponseDTO(
                d.getDepartmentId(),
                d.getName(),
                d.getDescription(),
                d.getMonthlyBudget(),
                d.getActive()
        );
    }
}