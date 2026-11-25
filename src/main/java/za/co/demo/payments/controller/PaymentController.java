package za.co.demo.payments.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.demo.payments.dto.PaymentRequest;
import za.co.demo.payments.dto.PaymentResponse;
import za.co.demo.payments.service.PaymentService;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

  private final PaymentService paymentService;

  public PaymentController(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  @PostMapping
  public ResponseEntity<PaymentResponse> createPayment(
      @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
      @Valid @RequestBody PaymentRequest request) {
    PaymentResponse response = paymentService.processPayment(idempotencyKey, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{idempotencyKey}")
  public ResponseEntity<PaymentResponse> getPayment(@PathVariable String idempotencyKey) {
    return ResponseEntity.ok(paymentService.getPaymentByIdempotencyKey(idempotencyKey));
  }
}