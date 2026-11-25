package za.co.demo.payments.exception;

public class DuplicateIdempotencyConflictException extends RuntimeException {
  public DuplicateIdempotencyConflictException(String message) {
    super(message);
  }
}