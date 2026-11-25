package za.co.demo.payments.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment_transactions", indexes = {
    @Index(name = "idx_payment_idempotency_key", columnList = "idempotencyKey", unique = true)
})
public class PaymentTransaction {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, unique = true, length = 100)
  private String idempotencyKey;

  @Column(nullable = false)
  private UUID sourceAccountId;

  @Column(nullable = false)
  private UUID destinationAccountId;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal amount;

  @Column(nullable = false, length = 3)
  private String currency;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PaymentStatus status;

  @Column(length = 255)
  private String failureReason;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  protected PaymentTransaction() {
  }

  public PaymentTransaction(
      String idempotencyKey,
      UUID sourceAccountId,
      UUID destinationAccountId,
      BigDecimal amount,
      String currency,
      PaymentStatus status,
      String failureReason) {
    this.idempotencyKey = idempotencyKey;
    this.sourceAccountId = sourceAccountId;
    this.destinationAccountId = destinationAccountId;
    this.amount = amount.setScale(4);
    this.currency = currency.toUpperCase();
    this.status = status;
    this.failureReason = failureReason;
    this.createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public String getIdempotencyKey() {
    return idempotencyKey;
  }

  public UUID getSourceAccountId() {
    return sourceAccountId;
  }

  public UUID getDestinationAccountId() {
    return destinationAccountId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public String getCurrency() {
    return currency;
  }

  public PaymentStatus getStatus() {
    return status;
  }

  public String getFailureReason() {
    return failureReason;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}