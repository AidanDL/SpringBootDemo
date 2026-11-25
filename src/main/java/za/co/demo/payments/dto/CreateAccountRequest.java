package za.co.demo.payments.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateAccountRequest(
    @NotBlank @Size(max = 100) String ownerName,
    @NotNull @DecimalMin(value = "0.0000", inclusive = true) BigDecimal initialBalance,
    @NotBlank @Size(min = 3, max = 3) String currency) {
}