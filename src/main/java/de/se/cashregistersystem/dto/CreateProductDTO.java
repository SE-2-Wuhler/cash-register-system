package de.se.cashregistersystem.dto;

public class CreateProductDTO {

    private String barcodeId;

    public String getBarcodeId() {
        return barcodeId;
    }

    public double getPrice() {
        return price;
    }

    private double price;

    public CreateProductDTO(String barcodeId, double price) {
        this.barcodeId = barcodeId;
        this.price = price;
    }
}
