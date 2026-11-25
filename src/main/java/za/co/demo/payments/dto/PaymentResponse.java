package za.co.demo.payments.dto;

import za.co.demo.payments.domain.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
    UUID transactionId,
    String idempotencyKey,
    PaymentStatus status,
    BigDecimal amount,
    String currency,
    String failureReason,
    Instant createdAt) {
}