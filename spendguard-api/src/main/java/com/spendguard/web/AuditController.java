package com.spendguard.web;

import com.spendguard.application.dto.response.AuditLogEntryDTO;
import com.spendguard.application.service.AuditService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audit")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping("/logs")
    @PreAuthorize("hasAnyRole('ADMIN','CFO')")
    public ResponseEntity<List<AuditLogEntryDTO>> getAllLogs() {
        return ResponseEntity.ok(auditService.getAllLogs());
    }

    @GetMapping("/logs/{requestId}")
    @PreAuthorize("hasAnyRole('ADMIN','CFO')")
    public ResponseEntity<List<AuditLogEntryDTO>> getLogsByRequestId(@PathVariable Long requestId) {
        return ResponseEntity.ok(auditService.getLogsByRequestId(requestId));
    }
}