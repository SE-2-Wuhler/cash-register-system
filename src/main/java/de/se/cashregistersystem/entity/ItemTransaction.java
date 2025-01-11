package de.se.cashregistersystem.entity;

import jakarta.persistence.*;
@Entity
@Table(name = "item_transaction")
public class ItemTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private TransactionRecord transactionRecord;

    @ManyToOne
    @JoinColumn(name = "itemid", nullable = false)
    private Item item;

// Getters and setters
}
