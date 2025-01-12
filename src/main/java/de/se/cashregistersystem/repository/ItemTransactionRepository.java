package de.se.cashregistersystem.repository;

import de.se.cashregistersystem.entity.Item;
import de.se.cashregistersystem.entity.ItemTransaction;
import de.se.cashregistersystem.entity.Pledge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ItemTransactionRepository  extends JpaRepository<ItemTransaction, UUID> {

    @Query("SELECT it.item FROM ItemTransaction it WHERE it.transactionRecord = :transactionId")
    List<UUID> getItemsByTransactionId(@Param("transactionId") UUID transactionId);

}


