package de.se.cashregistersystem.entity;

import de.se.cashregistersystem.util.Scanable;
import jakarta.persistence.*;

import java.util.UUID;


@Entity
@Table(name = "pledge")
public class Pledge implements Scanable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "barcode_id")
    private String barcodeId;
    @Column(name ="value")
    private double value;

    @Column(name ="validated")
    private boolean validated;

    public Pledge(){}

    public void setBarcodeId(String barcodeId) {
        this.barcodeId = barcodeId;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Pledge(String barcode_id, double value){
        this.barcodeId = barcode_id;
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
        return validated;
    }
    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    @Override
    public boolean isPledge() {
        return true;
    }
}
