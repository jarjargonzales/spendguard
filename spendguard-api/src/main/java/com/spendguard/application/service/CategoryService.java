package com.spendguard.application.service;

import com.spendguard.application.dto.request.CategoryDTO;
import com.spendguard.application.dto.response.CategoryResponseDTO;
import com.spendguard.application.port.ExpenseCategoryRepository;
import com.spendguard.domain.entity.ExpenseCategory;
import com.spendguard.domain.entity.User;
import com.spendguard.domain.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final ExpenseCategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> findAll() {
        return categoryRepository.findAll().stream()
                .filter(ExpenseCategory::getActive)
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public CategoryResponseDTO create(CategoryDTO dto, User creator) {
        ExpenseCategory category = new ExpenseCategory();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setRequiresApproval(dto.getRequiresApproval());
        category.setActive(true);
        category.setVersion("1");
        category.setCreatedBy(creator.getUserId());
        category.setUpdatedBy(creator.getUserId());
        category.setOwnerId(creator.getUserId());
        return toDTO(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponseDTO update(Long id, CategoryDTO dto, User updater) {
        ExpenseCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Categoría no encontrada"));
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setRequiresApproval(dto.getRequiresApproval());
        category.setUpdatedBy(updater.getUserId());
        return toDTO(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id, User deleter) {
        ExpenseCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Categoría no encontrada"));
        category.setActive(false);
        category.setUpdatedBy(deleter.getUserId());
        categoryRepository.save(category);
    }

    private CategoryResponseDTO toDTO(ExpenseCategory c) {
        return new CategoryResponseDTO(
                c.getCategoryId(),
                c.getName(),
                c.getDescription(),
                c.getRequiresApproval(),
                c.getActive()
        );
    }
}