package de.se.cashregistersystem.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "pledge_transaction")
public class PledgeTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "transaction_id", nullable = false)
    private UUID transaction;



    @Column(name = "pledgeid", nullable = false)
    private UUID pledge;

    public UUID getId() {
        return id;
    }

    public UUID getTransaction() {
        return transaction;
    }

    public void setTransaction(UUID transaction) {
        this.transaction = transaction;
    }

    public UUID getPledge() {
        return pledge;
    }

    public void setPledge(UUID pledge) {
        this.pledge = pledge;
    }
}
