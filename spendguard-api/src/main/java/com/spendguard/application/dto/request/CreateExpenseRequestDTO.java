package com.spendguard.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateExpenseRequestDTO {

    @NotNull(message = "El título es obligatorio")
    @Size(min = 3, max = 200, message = "El título debe tener entre 3 y 200 caracteres")
    private String title;

    private String description;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    private BigDecimal amount;

    @NotBlank(message = "La moneda es obligatoria")
    private String currency = "USD";

    @NotNull(message = "La categoría es obligatoria")
    private Long categoryId;

    @NotNull(message = "El departamento es obligatorio")
    private Long departmentId;
}