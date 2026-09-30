package com.spendguard.web;

import com.spendguard.application.dto.request.CategoryDTO;
import com.spendguard.application.dto.response.CategoryResponseDTO;
import com.spendguard.application.service.CategoryService;
import com.spendguard.application.service.SecurityService;
import com.spendguard.domain.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final SecurityService securityService;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> findAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CFO')")
    public ResponseEntity<CategoryResponseDTO> create(@Valid @RequestBody CategoryDTO dto) {
        User creator = securityService.getCurrentUser();
        return ResponseEntity.ok(categoryService.create(dto, creator));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CFO')")
    public ResponseEntity<CategoryResponseDTO> update(@PathVariable Long id, @Valid @RequestBody CategoryDTO dto) {
        User updater = securityService.getCurrentUser();
        return ResponseEntity.ok(categoryService.update(id, dto, updater));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CFO')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        User deleter = securityService.getCurrentUser();
        categoryService.delete(id, deleter);
        return ResponseEntity.noContent().build();
    }
}