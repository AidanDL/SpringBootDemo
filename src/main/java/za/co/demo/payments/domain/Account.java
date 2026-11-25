package za.co.demo.payments.domain;

import jakarta.persistence.*;
import za.co.demo.payments.exception.InsufficientFundsException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, unique = true, length = 100)
  private String ownerName;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal balance;

  @Column(nullable = false, length = 3)
  private String currency;

  @Version
  private Long version;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  protected Account() {
  }

  public Account(String ownerName, BigDecimal initialBalance, String currency) {
    this.ownerName = ownerName;
    this.balance = initialBalance.setScale(4);
    this.currency = currency.toUpperCase();
    this.createdAt = Instant.now();
  }

  public void debit(BigDecimal amount) {
    validatePositive(amount);
    if (this.balance.compareTo(amount) < 0) {
      throw new InsufficientFundsException("Insufficient balance in account " + id);
    }
    this.balance = this.balance.subtract(amount);
  }

  public void credit(BigDecimal amount) {
    validatePositive(amount);
    this.balance = this.balance.add(amount);
  }

  private void validatePositive(BigDecimal amount) {
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Amount must be greater than zero");
    }
  }

  public UUID getId() {
    return id;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public String getCurrency() {
    return currency;
  }

  public Long getVersion() {
    return version;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}