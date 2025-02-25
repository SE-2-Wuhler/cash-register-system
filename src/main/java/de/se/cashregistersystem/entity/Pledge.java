package de.se.cashregistersystem.entity;

import de.se.cashregistersystem.dto.Scanable;
import jakarta.persistence.*;

import java.util.UUID;


@Entity
@Table(name = "pledge")
public class Pledge implements Scanable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "transaction_id" )
    private UUID transactionId;

    @Column(name = "barcode_id")
    private String barcodeId;
    @Column(name ="value")
    private double value;



    public Pledge(){}

    public void setBarcodeId(String barcodeId) {
        this.barcodeId = barcodeId;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Pledge(double value){
        this.value = value;
    }
    public double getValue() {
        return value;
    }

    public UUID getId() {
        return id;
    }

    public String getBarcodeId() {
        return barcodeId;
    }

    public boolean isValidated() {
        return transactionId != null;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    @Override
    public boolean isPledge() {
        return true;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
