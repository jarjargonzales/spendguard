package com.spendguard.application.dto.request;

import com.spendguard.domain.enums.ApprovalAction;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ApprovalDecisionDTO {

    @NotNull(message = "La decisión es obligatoria")
    private ApprovalAction decision;

    private String comments;
}