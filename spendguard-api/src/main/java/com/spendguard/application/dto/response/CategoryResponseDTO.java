package com.spendguard.application.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CategoryResponseDTO {
    private Long categoryId;
    private String name;
    private String description;
    private Boolean requiresApproval;
    private Boolean active;
}