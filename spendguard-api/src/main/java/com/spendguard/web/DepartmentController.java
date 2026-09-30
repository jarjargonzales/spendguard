package com.spendguard.web;

import com.spendguard.application.dto.request.DepartmentDTO;
import com.spendguard.application.dto.response.DepartmentResponseDTO;
import com.spendguard.application.service.DepartmentService;
import com.spendguard.application.service.SecurityService;
import com.spendguard.domain.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;
    private final SecurityService securityService;

    @GetMapping
    public ResponseEntity<List<DepartmentResponseDTO>> findAll() {
        return ResponseEntity.ok(departmentService.findAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CFO')")
    public ResponseEntity<DepartmentResponseDTO> create(@Valid @RequestBody DepartmentDTO dto) {
        User creator = securityService.getCurrentUser();
        return ResponseEntity.ok(departmentService.create(dto, creator));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CFO')")
    public ResponseEntity<DepartmentResponseDTO> update(@PathVariable Long id, @Valid @RequestBody DepartmentDTO dto) {
        User updater = securityService.getCurrentUser();
        return ResponseEntity.ok(departmentService.update(id, dto, updater));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CFO')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        User deleter = securityService.getCurrentUser();
        departmentService.delete(id, deleter);
        return ResponseEntity.noContent().build();
    }
}