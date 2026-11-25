package za.co.demo.payments.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String ownerName,
        BigDecimal balance,
        String currency,
        Instant createdAt) {
}