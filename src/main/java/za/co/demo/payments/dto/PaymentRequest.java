package za.co.demo.payments.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(
    @NotNull UUID sourceAccountId,
    @NotNull UUID destinationAccountId,
    @NotNull @DecimalMin(value = "0.01", message = "Amount must be at least 0.01") BigDecimal amount,
    @NotBlank @Size(min = 3, max = 3) String currency) {
}