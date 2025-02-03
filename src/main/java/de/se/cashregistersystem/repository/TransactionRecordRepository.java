package de.se.cashregistersystem.repository;

import de.se.cashregistersystem.entity.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import de.se.cashregistersystem.entity.TransactionRecord;

public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, UUID> {

    @Query("SELECT tr FROM TransactionRecord tr WHERE tr.barcodeId = :barcodeId AND tr.status = 'paid'")
    Optional<TransactionRecord> findPaidTransactionRecord(String barcodeId);
}
