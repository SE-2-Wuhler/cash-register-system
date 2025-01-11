package de.se.cashregistersystem.repository;

import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.entity.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, UUID> {
}
