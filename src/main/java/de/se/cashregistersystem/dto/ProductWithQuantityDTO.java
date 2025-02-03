package de.se.cashregistersystem.dto;

import java.util.UUID;

public class ProductWithQuantityDTO {
    private UUID itemId;
    private int quantity;

    public UUID getItemId() {
        return itemId;
    }
    public int getQuantity() {
        return quantity;
    }

    public ProductWithQuantityDTO() {
    }

    public ProductWithQuantityDTO(int quantity) {
        this.quantity = quantity;
    }

}

