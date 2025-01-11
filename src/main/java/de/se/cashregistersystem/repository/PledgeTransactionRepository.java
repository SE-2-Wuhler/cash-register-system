package de.se.cashregistersystem.repository;

import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.entity.PledgeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PledgeTransactionRepository  extends JpaRepository<PledgeTransaction, UUID> {
}
