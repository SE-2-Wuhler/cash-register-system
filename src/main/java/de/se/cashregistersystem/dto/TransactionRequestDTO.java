package de.se.cashregistersystem.dto;

import java.util.UUID;

public class TransactionRequestDTO {

        protected ItemWithQuantityDTO[] items;
        protected UUID[] pledges;


    public ItemWithQuantityDTO[] getItems() {
        return items;
    }

    public void setItems(ItemWithQuantityDTO[] items) {
        this.items = items;
    }

    public UUID[] getPledges() {
        return pledges;
    }

    public void setPledges(UUID[] pledges) {
        this.pledges = pledges;
    }
}
