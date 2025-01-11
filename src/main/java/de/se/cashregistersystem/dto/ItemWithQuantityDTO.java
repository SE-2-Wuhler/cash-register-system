package de.se.cashregistersystem.dto;

import java.util.UUID;

public class ItemWithQuantityDTO {
    private UUID itemId;
    private int quantity;

    public UUID getItemId() {
        return itemId;
    }
    public int getQuantity() {
        return quantity;
    }

    public ItemWithQuantityDTO() {
    }

    public ItemWithQuantityDTO(int quantity) {
        this.quantity = quantity;
    }
}

