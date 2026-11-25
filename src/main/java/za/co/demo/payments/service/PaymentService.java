package za.co.demo.payments.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.demo.payments.domain.*;
import za.co.demo.payments.dto.AccountResponse;
import za.co.demo.payments.dto.CreateAccountRequest;
import za.co.demo.payments.dto.LedgerListResponse;
import za.co.demo.payments.dto.LedgerResponse;
import za.co.demo.payments.dto.PaymentRequest;
import za.co.demo.payments.dto.PaymentResponse;
import za.co.demo.payments.exception.DuplicateIdempotencyConflictException;
import za.co.demo.payments.exception.ResourceNotFoundException;
import za.co.demo.payments.repository.AccountRepository;
import za.co.demo.payments.repository.LedgerEntryRepository;
import za.co.demo.payments.repository.PaymentTransactionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

  private final AccountRepository accountRepository;
  private final PaymentTransactionRepository paymentRepository;
  private final LedgerEntryRepository ledgerEntryRepository;

  public PaymentService(
      AccountRepository accountRepository,
      PaymentTransactionRepository paymentRepository,
      LedgerEntryRepository ledgerEntryRepository) {
    this.accountRepository = accountRepository;
    this.paymentRepository = paymentRepository;
    this.ledgerEntryRepository = ledgerEntryRepository;
  }

  @Transactional
  public AccountResponse createAccount(CreateAccountRequest request) {
    Account saved = accountRepository.save(
        new Account(request.ownerName(), request.initialBalance(), request.currency()));
    return toAccountResponse(saved);
  }

  @Transactional(readOnly = true)
  public AccountResponse getAccount(UUID accountId) {
    Account account = accountRepository.findById(accountId)
        .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountId));
    return toAccountResponse(account);
  }

  @Transactional
  public PaymentResponse processPayment(String idempotencyKey, PaymentRequest request) {
    if (idempotencyKey == null || idempotencyKey.isBlank()) {
      throw new IllegalArgumentException("Idempotency-Key header is required");
    }
    if (request.sourceAccountId().equals(request.destinationAccountId())) {
      throw new IllegalArgumentException("Source and destination accounts must be different");
    }

    // 1. Return existing result if idempotency key was already processed
    var existing = paymentRepository.findByIdempotencyKey(idempotencyKey);
    if (existing.isPresent()) {
      return toPaymentResponse(existing.get());
    }

    Account source = accountRepository.findById(request.sourceAccountId())
        .orElseThrow(() -> new ResourceNotFoundException("Source account not found: " + request.sourceAccountId()));
    Account destination = accountRepository.findById(request.destinationAccountId())
        .orElseThrow(
            () -> new ResourceNotFoundException("Destination account not found: " + request.destinationAccountId()));

    if (!source.getCurrency().equalsIgnoreCase(request.currency())
        || !destination.getCurrency().equalsIgnoreCase(request.currency())) {
      throw new IllegalArgumentException("Currency mismatch between payment and accounts");
    }

    // 2. Debit source & credit destination (enforces balance check + @Version
    // optimistic lock)
    source.debit(request.amount());
    destination.credit(request.amount());
    accountRepository.saveAll(List.of(source, destination));

    // 3. Persist completed payment with unique idempotency key constraint
    PaymentTransaction tx = new PaymentTransaction(
        idempotencyKey,
        source.getId(),
        destination.getId(),
        request.amount(),
        request.currency(),
        PaymentStatus.COMPLETED,
        null);

    PaymentTransaction savedTx;
    try {
      savedTx = paymentRepository.saveAndFlush(tx);
    } catch (DataIntegrityViolationException ex) {
      throw new DuplicateIdempotencyConflictException("Duplicate idempotency key in flight: " + idempotencyKey);
    }

    // 4. Write double-entry ledger records
    ledgerEntryRepository.saveAll(List.of(
        new LedgerEntry(savedTx.getId(), source.getId(), LedgerEntryType.DEBIT, request.amount()),
        new LedgerEntry(savedTx.getId(), destination.getId(), LedgerEntryType.CREDIT, request.amount())));

    return toPaymentResponse(savedTx);
  }

  @Transactional(readOnly = true)
  public PaymentResponse getPaymentByIdempotencyKey(String idempotencyKey) {
    PaymentTransaction tx = paymentRepository.findByIdempotencyKey(idempotencyKey)
        .orElseThrow(() -> new ResourceNotFoundException("Payment not found for key: " + idempotencyKey));
    return toPaymentResponse(tx);
  }

  @Transactional(readOnly = true)
  public LedgerListResponse getLedgerEntries(UUID accountId) {
    List<LedgerEntry> entries = ledgerEntryRepository.findByAccountId(accountId);
    return toLedgerResponseList(entries);
  }

  private AccountResponse toAccountResponse(Account a) {
    return new AccountResponse(a.getId(), a.getOwnerName(), a.getBalance(), a.getCurrency(), a.getCreatedAt());
  }

  private PaymentResponse toPaymentResponse(PaymentTransaction tx) {
    return new PaymentResponse(
        tx.getId(),
        tx.getIdempotencyKey(),
        tx.getStatus(),
        tx.getAmount(),
        tx.getCurrency(),
        tx.getFailureReason(),
        tx.getCreatedAt());
  }

  private LedgerListResponse toLedgerResponseList(List<LedgerEntry> entries) {
    List<LedgerResponse> list = new ArrayList<LedgerResponse>();

    for (LedgerEntry entry : entries) {
      list.add(
          new LedgerResponse(
              entry.getTransactionId(),
              entry.getAccountId(),
              entry.getEntryType(),
              entry.getAmount()));
    }

    return new LedgerListResponse(list);
  }
}