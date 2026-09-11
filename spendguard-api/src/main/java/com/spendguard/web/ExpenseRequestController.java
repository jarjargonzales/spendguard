package com.spendguard.web;

import com.spendguard.application.dto.request.CreateExpenseRequestDTO;
import com.spendguard.application.dto.response.ExpenseRequestResponseDTO;
import com.spendguard.application.port.UserRepository;
import com.spendguard.application.service.ExpenseRequestService;
import com.spendguard.domain.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/expense-requests")
public class ExpenseRequestController {

    private final ExpenseRequestService requestService;
    private final UserRepository userRepository;

    public ExpenseRequestController(ExpenseRequestService requestService,
                                    UserRepository userRepository) {
        this.requestService = requestService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<ExpenseRequestResponseDTO> create(
            @Valid @RequestBody CreateExpenseRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        User requester = getCurrentUser(userDetails);
        return ResponseEntity.ok(requestService.create(dto, requester));
    }

    @GetMapping
    public ResponseEntity<List<ExpenseRequestResponseDTO>> getAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(requestService.getAllForRequester(user.getUserId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseRequestResponseDTO> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(requestService.getById(id, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseRequestResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CreateExpenseRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(requestService.update(id, dto, user));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<ExpenseRequestResponseDTO> submit(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(requestService.submit(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        requestService.cancel(id, user);
        return ResponseEntity.noContent().build();
    }

    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}