package de.se.cashregistersystem.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "pledge_transaction")
public class PledgeTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private TransactionRecord transaction;

    @ManyToOne
    @JoinColumn(name = "pledgeid", nullable = false)
    private Pledge pledge;

}
