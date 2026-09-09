package com.spendguard.application.dto.response;

import com.spendguard.domain.enums.RequestStatus;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ExpenseRequestResponseDTO {

    private Long requestId;
    private String title;
    private String description;
    private BigDecimal amount;
    private String currency;
    private RequestStatus status;
    private Timestamp submissionDate;
    private Timestamp resolutionDate;
    private Long requesterId;
    private String requesterName;
    private Long departmentId;
    private String departmentName;
    private Long categoryId;
    private String categoryName;
}