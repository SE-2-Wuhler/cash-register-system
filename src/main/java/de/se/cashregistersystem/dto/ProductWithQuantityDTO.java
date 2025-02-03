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

    public ProductWithQuantityDTO(UUID itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

}

