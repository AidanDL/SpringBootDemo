package za.co.demo.payments.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ledger_entries")
public class LedgerEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private UUID transactionId;

  @Column(nullable = false)
  private UUID accountId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private LedgerEntryType entryType;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal amount;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  protected LedgerEntry() {
  }

  public LedgerEntry(UUID transactionId, UUID accountId, LedgerEntryType entryType, BigDecimal amount) {
    this.transactionId = transactionId;
    this.accountId = accountId;
    this.entryType = entryType;
    this.amount = amount.setScale(4);
    this.createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public UUID getTransactionId() {
    return transactionId;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public LedgerEntryType getEntryType() {
    return entryType;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}