package com.spendguard.application.dto.response;

import lombok.*;

import java.sql.Timestamp;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ApprovalResponseDTO {

    private Long approvalId;
    private Long requestId;
    private Long approverId;
    private String approverName;
    private String decision;
    private String comments;
    private Timestamp decidedAt;
    private Integer sequenceOrder;
}