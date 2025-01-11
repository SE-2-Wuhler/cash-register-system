package de.se.cashregistersystem.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "item_transaction")
public class ItemTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;



    @Column(name = "transaction_id", nullable = false)
    private UUID transactionRecord;

    @Column(name = "itemid", nullable = false)
    private UUID item;

    public UUID getTransactionRecord() {
        return transactionRecord;
    }

    public void setTransactionRecord(UUID transactionRecord) {
        this.transactionRecord = transactionRecord;
    }

    public UUID getItem() {
        return item;
    }

    public void setItem(UUID item) {
        this.item = item;
    }
}
