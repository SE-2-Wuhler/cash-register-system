package de.se.cashregistersystem.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_transaction")
public class ProductTransaction {
    public ProductTransaction(UUID transactionRecordId, UUID productId) {
        this.transactionRecordId = transactionRecordId;
        this.productId = productId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)

    @Column(name = "transaction_id", nullable = false)
    private UUID transactionRecordId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    public UUID getTransactionRecordId() {
        return transactionRecordId;
    }

    public void setTransactionRecordId(UUID transactionRecordId) {
        this.transactionRecordId = transactionRecordId;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }
}
