package de.se.cashregistersystem.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "transaction_record")
public class TransactionRecord {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "barcode_id")
    private String barcodeId;

    @Column(name = "status")
    private String status;

    public TransactionRecord(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public TransactionRecord() {
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public UUID getId() {
        return id;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public String getBarcodeId() {
        return barcodeId;
    }

    public void setBarcodeId(String barcodeId) {
        this.barcodeId = barcodeId;
    }

}
