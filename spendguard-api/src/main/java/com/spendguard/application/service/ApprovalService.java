package com.spendguard.application.service;

import com.spendguard.application.dto.request.ApprovalDecisionDTO;
import com.spendguard.application.dto.response.ApprovalResponseDTO;
import com.spendguard.application.port.ApprovalRepository;
import com.spendguard.application.port.ExpenseRequestRepository;
import com.spendguard.domain.entity.Approval;
import com.spendguard.domain.entity.ExpenseRequest;
import com.spendguard.domain.entity.User;
import com.spendguard.domain.enums.ApprovalAction;
import com.spendguard.domain.enums.RequestStatus;
import com.spendguard.domain.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final ApprovalRepository approvalRepository;
    private final ExpenseRequestRepository requestRepository;
    private final AuditService auditService;
    private final BudgetService budgetService;

    @Transactional
    public void processDecision(Long requestId, User approver, ApprovalDecisionDTO decisionDTO) {
        ExpenseRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException("Solicitud no encontrada"));

        if (request.getStatus() != RequestStatus.UNDER_REVIEW) {
            throw new BusinessException("La solicitud no está en revisión");
        }

        Approval approval = new Approval();
        approval.setRequest(request);
        approval.setApprover(approver);
        approval.setDecision(decisionDTO.getDecision().name());
        approval.setComments(decisionDTO.getComments());
        approval.setDecidedAt(new Timestamp(System.currentTimeMillis()));
        approval.setSequenceOrder(1);
        approval.setVersion("1");
        approval.setCreatedBy(approver.getUserId());
        approval.setUpdatedBy(approver.getUserId());
        approval.setOwnerId(approver.getUserId());

        approvalRepository.save(approval);

        if (decisionDTO.getDecision() == ApprovalAction.APPROVE) {
            request.setStatus(RequestStatus.APPROVED);
            request.setResolutionDate(new Timestamp(System.currentTimeMillis()));
            request.setUpdatedBy(approver.getUserId());
            requestRepository.save(request);
            budgetService.consumeBudget(request);
        } else if (decisionDTO.getDecision() == ApprovalAction.REJECT) {
            request.setStatus(RequestStatus.REJECTED);
            request.setResolutionDate(new Timestamp(System.currentTimeMillis()));
            request.setUpdatedBy(approver.getUserId());
            requestRepository.save(request);
        } else if (decisionDTO.getDecision() == ApprovalAction.ESCALATE) {
            Approval escalated = new Approval();
            escalated.setRequest(request);
            escalated.setApprover(approver);
            escalated.setDecision("PENDING");
            escalated.setSequenceOrder(2);
            escalated.setVersion("1");
            escalated.setCreatedBy(approver.getUserId());
            escalated.setUpdatedBy(approver.getUserId());
            escalated.setOwnerId(approver.getUserId());
            approvalRepository.save(escalated);
        }

        auditService.logEvent(request, approver, decisionDTO.getDecision().name(), request.getStatus().name());
    }

    @Transactional(readOnly = true)
    public List<ApprovalResponseDTO> getPendingApprovalsForUser(Long userId) {
        return approvalRepository.findByApprover_UserIdAndDecision(userId, "PENDING")
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ApprovalResponseDTO> getApprovalHistoryForUser(Long userId) {
        return approvalRepository.findByApprover_UserIdAndDecision(userId, "APPROVED")
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private ApprovalResponseDTO toDTO(Approval approval) {
        return new ApprovalResponseDTO(
                approval.getApprovalId(),
                approval.getRequest().getRequestId(),
                approval.getApprover().getUserId(),
                approval.getApprover().getUsername(),
                approval.getDecision(),
                approval.getComments(),
                approval.getDecidedAt(),
                approval.getSequenceOrder()
        );
    }
}