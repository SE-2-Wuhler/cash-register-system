package de.se.cashregistersystem.entity;

import jakarta.persistence.*;

import java.util.UUID;


@Entity
@Table(name = "pledge")
public class Pledge {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


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

    public Pledge(int barcode_id, double value){
        this.barcodeId = barcodeId;
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
}
