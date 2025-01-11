package de.se.cashregistersystem.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "item_transaction")
public class ItemTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private TransactionRecord transactionRecord;

    @ManyToOne
    @JoinColumn(name = "itemid", nullable = false)
    private Item item;

// Getters and setters
}
