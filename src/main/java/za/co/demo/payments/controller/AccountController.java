package za.co.demo.payments.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.demo.payments.dto.AccountResponse;
import za.co.demo.payments.dto.CreateAccountRequest;
import za.co.demo.payments.dto.LedgerListResponse;
import za.co.demo.payments.service.PaymentService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

  private final PaymentService paymentService;

  public AccountController(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  @PostMapping
  public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createAccount(request));
  }

  @GetMapping("/{accountId}")
  public ResponseEntity<AccountResponse> getAccount(@PathVariable UUID accountId) {
    return ResponseEntity.ok(paymentService.getAccount(accountId));
  }

  @GetMapping("/{accountId}/ledger")
  public ResponseEntity<LedgerListResponse> getLedgerEntries(@PathVariable UUID accountId) {
    return ResponseEntity.ok(paymentService.getLedgerEntries(accountId));
  }
}