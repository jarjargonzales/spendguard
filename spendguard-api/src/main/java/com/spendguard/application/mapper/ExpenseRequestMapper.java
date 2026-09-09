package com.spendguard.application.mapper;

import com.spendguard.application.dto.request.CreateExpenseRequestDTO;
import com.spendguard.application.dto.response.ExpenseRequestResponseDTO;
import com.spendguard.domain.entity.Department;
import com.spendguard.domain.entity.ExpenseCategory;
import com.spendguard.domain.entity.ExpenseRequest;
import com.spendguard.domain.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ExpenseRequestMapper {

	@Mapping(target = "description", source = "dto.description")
    @Mapping(target = "requestId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "submissionDate", ignore = true)
    @Mapping(target = "resolutionDate", ignore = true)
    @Mapping(target = "versionOpt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updated", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "requester", source = "requester")
    @Mapping(target = "department", source = "department")
    @Mapping(target = "category", source = "category")
	ExpenseRequest toEntity(CreateExpenseRequestDTO dto, User requester, Department department, ExpenseCategory category);

    @Mapping(target = "requesterId", source = "requester.userId")
    @Mapping(target = "requesterName", expression = "java(request.getRequester().getFirstName() + \" \" + request.getRequester().getLastName())")
    @Mapping(target = "departmentId", source = "department.departmentId")
    @Mapping(target = "departmentName", source = "department.name")
    @Mapping(target = "categoryId", source = "category.categoryId")
    @Mapping(target = "categoryName", source = "category.name")
    ExpenseRequestResponseDTO toResponseDTO(ExpenseRequest request);
}