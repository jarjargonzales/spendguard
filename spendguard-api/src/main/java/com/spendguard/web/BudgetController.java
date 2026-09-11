package com.spendguard.web;

import com.spendguard.application.dto.response.BudgetStatusDTO;
import com.spendguard.application.service.BudgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping("/status")
    public ResponseEntity<BudgetStatusDTO> getMyDepartmentStatus(
            @RequestParam Long departmentId) {
        return ResponseEntity.ok(budgetService.getStatus(departmentId));
    }

    @GetMapping("/{deptId}/status")
    @PreAuthorize("hasAnyRole('CFO','DIRECTOR')")
    public ResponseEntity<BudgetStatusDTO> getDepartmentStatus(@PathVariable Long deptId) {
        return ResponseEntity.ok(budgetService.getStatus(deptId));
    }
}