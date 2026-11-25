package za.co.demo.payments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.demo.payments.domain.Account;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
}