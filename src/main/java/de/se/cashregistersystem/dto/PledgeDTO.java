package de.se.cashregistersystem.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
public class PledgeDTO {
    private String id;
    private int barcodeId;


    public int getBarcodeId() {
        return barcodeId;
    }

    public void setBarcodeId(int barcodeId) {
        this.barcodeId = barcodeId;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    private double value;

    // Optional: Konstruktor ohne ID für die Erstellung neuer Pledges
    public PledgeDTO(int barcodeId, double value) {
        this.barcodeId = barcodeId;
        this.value = value;
    }
}