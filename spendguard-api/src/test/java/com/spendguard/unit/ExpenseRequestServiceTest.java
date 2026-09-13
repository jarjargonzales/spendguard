package com.spendguard.unit;

import com.spendguard.application.dto.request.CreateExpenseRequestDTO;
import com.spendguard.application.dto.response.ExpenseRequestResponseDTO;
import com.spendguard.application.mapper.ExpenseRequestMapper;
import com.spendguard.application.port.DepartmentRepository;
import com.spendguard.application.port.ExpenseCategoryRepository;
import com.spendguard.application.port.ExpenseRequestRepository;
import com.spendguard.application.service.ExpenseRequestService;
import com.spendguard.domain.entity.*;
import com.spendguard.domain.enums.RequestStatus;
import com.spendguard.domain.enums.UserRole;
import com.spendguard.domain.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExpenseRequestServiceTest {

    @Mock private ExpenseRequestRepository requestRepository;
    @Mock private ExpenseCategoryRepository categoryRepository;
    @Mock private DepartmentRepository departmentRepository;
    @Mock private ExpenseRequestMapper mapper;

    @InjectMocks private ExpenseRequestService service;

    private User requester;
    private Department department;
    private ExpenseCategory category;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        requester = new User();
        requester.setUserId(1L);
        requester.setUsername("juan");
        requester.setRole(UserRole.EMPLOYEE);

        department = new Department();
        department.setDepartmentId(10L);
        department.setName("Ingeniería");

        category = new ExpenseCategory();
        category.setCategoryId(20L);
        category.setName("Capacitación");
    }

    @Test
    void create_shouldReturnDraftRequest() {
        CreateExpenseRequestDTO dto = new CreateExpenseRequestDTO();
        dto.setTitle("Curso Java");
        dto.setAmount(new BigDecimal("800"));
        dto.setCurrency("USD");
        dto.setCategoryId(20L);
        dto.setDepartmentId(10L);

        when(departmentRepository.findById(10L)).thenReturn(Optional.of(department));
        when(categoryRepository.findById(20L)).thenReturn(Optional.of(category));
        when(mapper.toEntity(any(), any(), any(), any())).thenReturn(new ExpenseRequest());
        when(requestRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(mapper.toResponseDTO(any())).thenReturn(new ExpenseRequestResponseDTO());

        var result = service.create(dto, requester);

        assertNotNull(result);
        verify(requestRepository, times(1)).save(any());
    }

    @Test
    void create_shouldThrowWhenDepartmentNotFound() {
        CreateExpenseRequestDTO dto = new CreateExpenseRequestDTO();
        dto.setDepartmentId(999L);
        dto.setCategoryId(20L);

        when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> service.create(dto, requester));
    }

    @Test
    void submit_shouldFailIfNotDraft() {
        ExpenseRequest request = new ExpenseRequest();
        request.setRequestId(5L);
        request.setRequester(requester);
        request.setStatus(RequestStatus.APPROVED);

        when(requestRepository.findById(5L)).thenReturn(Optional.of(request));

        assertThrows(BusinessException.class, () -> service.submit(5L, requester));
    }
}