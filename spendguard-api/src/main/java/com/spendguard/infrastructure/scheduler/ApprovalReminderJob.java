package com.spendguard.infrastructure.scheduler;

import com.spendguard.application.port.ApprovalRepository;
import com.spendguard.application.service.AuditService;
import com.spendguard.application.service.NotificationService;
import com.spendguard.domain.entity.Approval;
import com.spendguard.domain.entity.ExpenseRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApprovalReminderJob {

    private final ApprovalRepository approvalRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;

    /**
     * Recordatorio a aprobadores con aprobaciones PENDING de más de 48 horas.
     * Se ejecuta cada 6 horas.
     */
    @Scheduled(cron = "0 0 */6 * * *")
    @Transactional
    public void sendReminders() {
        Timestamp threshold = Timestamp.from(Instant.now().minus(48, ChronoUnit.HOURS));
        List<Approval> pending = approvalRepository.findByDecisionAndCreatedBefore("PENDING", threshold);

        for (Approval approval : pending) {
            try {
                ExpenseRequest request = approval.getRequest();
                String msg = String.format("Recordatorio: la solicitud '%s' (#%d) sigue pendiente de tu aprobación.",
                        request.getTitle(), request.getRequestId());
                notificationService.notifyApprover(approval.getApprover(), msg);
                auditService.logEvent(request, approval.getApprover(), "REMINDER_SENT", request.getStatus().name());
            } catch (Exception e) {
                log.error("Error enviando recordatorio para approval {}: {}", approval.getApprovalId(), e.getMessage());
            }
        }
        log.info("Job de recordatorios ejecutado. Aprobaciones pendientes > 48h: {}", pending.size());
    }

    /**
     * Escalación automática para aprobaciones PENDING de más de 72 horas.
     * Se ejecuta cada 12 horas.
     */
    @Scheduled(cron = "0 0 */12 * * *")
    @Transactional
    public void escalateOverdue() {
        Timestamp threshold = Timestamp.from(Instant.now().minus(72, ChronoUnit.HOURS));
        List<Approval> overdue = approvalRepository.findByDecisionAndCreatedBefore("PENDING", threshold);

        for (Approval approval : overdue) {
            try {
                approval.setDecision("ESCALATED");
                approval.setUpdated(new Timestamp(System.currentTimeMillis()));
                approvalRepository.save(approval);
                ExpenseRequest request = approval.getRequest();
                auditService.logEvent(request, approval.getApprover(), "ESCALATED", request.getStatus().name());
                log.info("Aprobación {} escalada automáticamente por exceder 72h.", approval.getApprovalId());
            } catch (Exception e) {
                log.error("Error escalando approval {}: {}", approval.getApprovalId(), e.getMessage());
            }
        }
        log.info("Job de escalación ejecutado. Aprobaciones escaladas: {}", overdue.size());
    }
}