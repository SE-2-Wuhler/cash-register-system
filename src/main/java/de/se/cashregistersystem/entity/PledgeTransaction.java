package de.se.cashregistersystem.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "pledge_transaction")
public class PledgeTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;



    @ManyToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private TransactionRecord transaction;

    @ManyToOne
    @JoinColumn(name = "pledgeid", nullable = false)
    private Pledge pledge;

    public UUID getId() {
        return id;
    }


    public TransactionRecord getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionRecord transaction) {
        this.transaction = transaction;
    }

    public Pledge getPledge() {
        return pledge;
    }

    public void setPledge(Pledge pledge) {
        this.pledge = pledge;
    }

}
