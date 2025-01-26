package de.se.cashregistersystem.repository;

import de.se.cashregistersystem.entity.ProductTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductTransactionRepository extends JpaRepository<ProductTransaction, UUID> {

    @Query("SELECT it.item FROM ProductTransaction it WHERE it.transactionRecord = :transactionId")
    List<UUID> getItemsByTransactionId(@Param("transactionId") UUID transactionId);

}


