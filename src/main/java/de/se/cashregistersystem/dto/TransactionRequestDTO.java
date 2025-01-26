package de.se.cashregistersystem.dto;

import java.util.UUID;

public class TransactionRequestDTO {

        protected ProductWithQuantityDTO[] items;
        protected UUID[] pledges;


    public ProductWithQuantityDTO[] getItems() {
        return items;
    }

    public void setItems(ProductWithQuantityDTO[] items) {
        this.items = items;
    }

    public UUID[] getPledges() {
        return pledges;
    }

    public void setPledges(UUID[] pledges) {
        this.pledges = pledges;
    }
}
