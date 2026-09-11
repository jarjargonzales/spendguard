package com.spendguard.web;

import com.spendguard.application.dto.request.ApprovalDecisionDTO;
import com.spendguard.application.dto.response.ApprovalResponseDTO;
import com.spendguard.application.service.ApprovalService;
import com.spendguard.domain.entity.User;
import com.spendguard.application.port.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/approvals")
public class ApprovalController {

    private final ApprovalService approvalService;
    private final UserRepository userRepository;

    public ApprovalController(ApprovalService approvalService, UserRepository userRepository) {
        this.approvalService = approvalService;
        this.userRepository = userRepository;
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ApprovalResponseDTO>> getPending(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        List<ApprovalResponseDTO> pendings = approvalService.getPendingApprovalsForUser(user.getUserId());
        return ResponseEntity.ok(pendings);
    }

    @PostMapping("/{requestId}")
    public ResponseEntity<Void> decide(
            @PathVariable Long requestId,
            @Valid @RequestBody ApprovalDecisionDTO decisionDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        approvalService.processDecision(requestId, user, decisionDTO);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/history")
    public ResponseEntity<List<ApprovalResponseDTO>> history(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        // Devolver historial de aprobaciones del usuario
        List<ApprovalResponseDTO> history = approvalService.getApprovalHistoryForUser(user.getUserId());
        return ResponseEntity.ok(history);
    }
}