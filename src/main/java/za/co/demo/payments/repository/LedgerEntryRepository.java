package za.co.demo.payments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.demo.payments.domain.LedgerEntry;

import java.util.List;
import java.util.UUID;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, UUID> {
  List<LedgerEntry> findByAccountId(UUID accountId);
}