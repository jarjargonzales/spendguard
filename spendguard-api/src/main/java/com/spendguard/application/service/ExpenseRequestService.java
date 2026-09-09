package com.spendguard.application.service;

import com.spendguard.application.dto.request.CreateExpenseRequestDTO;
import com.spendguard.application.dto.response.ExpenseRequestResponseDTO;
import com.spendguard.application.mapper.ExpenseRequestMapper;
import com.spendguard.application.port.DepartmentRepository;
import com.spendguard.application.port.ExpenseCategoryRepository;
import com.spendguard.application.port.ExpenseRequestRepository;
import com.spendguard.domain.entity.Department;
import com.spendguard.domain.entity.ExpenseCategory;
import com.spendguard.domain.entity.ExpenseRequest;
import com.spendguard.domain.entity.User;
import com.spendguard.domain.enums.RequestStatus;
import com.spendguard.domain.exception.BusinessException;
import com.spendguard.domain.exception.RequestNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseRequestService {

    private final ExpenseRequestRepository requestRepository;
    private final ExpenseCategoryRepository categoryRepository;
    private final DepartmentRepository departmentRepository;
    private final ExpenseRequestMapper mapper;

    @Transactional
    public ExpenseRequestResponseDTO create(CreateExpenseRequestDTO dto, User requester) {
        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new BusinessException("Departamento no encontrado"));
        ExpenseCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new BusinessException("Categoría no encontrada"));

        ExpenseRequest request = mapper.toEntity(dto, requester, department, category);
        request.setStatus(RequestStatus.DRAFT);
        request.setVersionOpt(0);
        request.setVersion("1");
        request.setCreatedBy(requester.getUserId());
        request.setUpdatedBy(requester.getUserId());
        request.setOwnerId(requester.getUserId());

        ExpenseRequest saved = requestRepository.save(request);
        return mapper.toResponseDTO(saved);
    }

    @Transactional
    public ExpenseRequestResponseDTO update(Long id, CreateExpenseRequestDTO dto, User requester) {
        ExpenseRequest request = getRequest(id, requester);
        if (request.getStatus() != RequestStatus.DRAFT) {
            throw new BusinessException("Solo se puede editar una solicitud en estado DRAFT");
        }

        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new BusinessException("Departamento no encontrado"));
        ExpenseCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new BusinessException("Categoría no encontrada"));

        request.setTitle(dto.getTitle());
        request.setDescription(dto.getDescription());
        request.setAmount(dto.getAmount());
        request.setCurrency(dto.getCurrency());
        request.setDepartment(department);
        request.setCategory(category);
        request.setUpdatedBy(requester.getUserId());

        ExpenseRequest saved = requestRepository.save(request);
        return mapper.toResponseDTO(saved);
    }

    @Transactional
    public ExpenseRequestResponseDTO submit(Long id, User requester) {
        ExpenseRequest request = getRequest(id, requester);
        if (request.getStatus() != RequestStatus.DRAFT) {
            throw new BusinessException("Solo se puede enviar una solicitud en estado DRAFT");
        }

        request.setStatus(RequestStatus.SUBMITTED);
        request.setSubmissionDate(new java.sql.Timestamp(System.currentTimeMillis()));
        request.setUpdatedBy(requester.getUserId());

        ExpenseRequest saved = requestRepository.save(request);
        return mapper.toResponseDTO(saved);
    }

    @Transactional
    public ExpenseRequestResponseDTO cancel(Long id, User requester) {
        ExpenseRequest request = getRequest(id, requester);
        if (request.getStatus() == RequestStatus.UNDER_REVIEW ||
            request.getStatus() == RequestStatus.APPROVED ||
            request.getStatus() == RequestStatus.PAID) {
            throw new BusinessException("No se puede cancelar una solicitud en revisión, aprobada o pagada");
        }

        request.setStatus(RequestStatus.CANCELLED);
        request.setUpdatedBy(requester.getUserId());

        ExpenseRequest saved = requestRepository.save(request);
        return mapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public ExpenseRequestResponseDTO getById(Long id, User requester) {
        ExpenseRequest request = getRequest(id, requester);
        return mapper.toResponseDTO(request);
    }

    @Transactional(readOnly = true)
    public List<ExpenseRequestResponseDTO> getAllForRequester(Long requesterId) {
        return requestRepository.findByRequester_UserId(requesterId).stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ExpenseRequestResponseDTO> getAll() {
        return requestRepository.findByStatus(null).stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    private ExpenseRequest getRequest(Long id, User requester) {
        ExpenseRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new RequestNotFoundException("Solicitud no encontrada"));
        if (!request.getRequester().getUserId().equals(requester.getUserId())) {
            throw new BusinessException("No tienes permiso para ver esta solicitud");
        }
        return request;
    }
}