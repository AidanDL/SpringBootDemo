package za.co.demo.payments.dto;

import java.math.BigDecimal;

import java.util.UUID;
import za.co.demo.payments.domain.LedgerEntryType;

public record LedgerResponse(
    UUID transactionId,
    UUID accountId,
    LedgerEntryType entryType,
    BigDecimal amount) {
}
