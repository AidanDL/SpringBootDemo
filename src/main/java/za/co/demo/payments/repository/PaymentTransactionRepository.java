package za.co.demo.payments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.demo.payments.domain.PaymentTransaction;

import java.util.Optional;
import java.util.UUID;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {
  Optional<PaymentTransaction> findByIdempotencyKey(String idempotencyKey);
}