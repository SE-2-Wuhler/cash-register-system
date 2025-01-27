package de.se.cashregistersystem.dto;

public class CreateProductDTO {

    private String barcodeId;
    private double price;
    private double pledgeValue;

    public CreateProductDTO(String barcodeId, double price, double pledgeValue) {
        this.barcodeId = barcodeId;
        this.price = price;
        this.pledgeValue = pledgeValue;
    }
    public String getBarcodeId() {
        return barcodeId;
    }

    public double getPrice() {
        return price;
    }

    public double getPledgeValue() {
        return pledgeValue;
    }

    public void setPledgeValue(double pledgeValue) {
        this.pledgeValue = pledgeValue;
    }
}
