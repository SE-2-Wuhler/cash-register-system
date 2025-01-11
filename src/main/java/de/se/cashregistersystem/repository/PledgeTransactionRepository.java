package de.se.cashregistersystem.repository;

import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.entity.PledgeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PledgeTransactionRepository  extends JpaRepository<PledgeTransaction, UUID> {
}
