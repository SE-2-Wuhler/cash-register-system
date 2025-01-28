package de.se.cashregistersystem.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import de.se.cashregistersystem.entity.TransactionRecord;

public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, UUID> {
}
