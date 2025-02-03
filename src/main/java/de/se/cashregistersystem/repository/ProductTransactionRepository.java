package de.se.cashregistersystem.repository;

import de.se.cashregistersystem.entity.ProductTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductTransactionRepository extends JpaRepository<ProductTransaction, UUID> {

    @Query("SELECT it.productId FROM ProductTransaction it WHERE it.transactionRecordId = :transactionId")
    Optional<List<UUID>> getProductsByTransactionId(@Param("transactionId") UUID transactionId);

    void deleteByTransactionRecordId(UUID transactionId);

}


