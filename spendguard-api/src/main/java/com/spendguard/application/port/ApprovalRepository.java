package com.spendguard.application.port;

import com.spendguard.domain.entity.Approval;

import java.util.List;
import java.util.Optional;

public interface ApprovalRepository {
    Approval save(Approval approval);
    Optional<Approval> findById(Long id);
    List<Approval> findByRequest_RequestId(Long requestId);
    List<Approval> findByApprover_UserIdAndDecision(Long approverId, String decision);
    List<Approval> findByRequest_RequestIdAndDecision(Long requestId, String decision);
}