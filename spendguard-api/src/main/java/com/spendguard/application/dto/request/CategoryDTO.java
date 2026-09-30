package com.spendguard.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CategoryDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String description;

    private Boolean requiresApproval = true;
}