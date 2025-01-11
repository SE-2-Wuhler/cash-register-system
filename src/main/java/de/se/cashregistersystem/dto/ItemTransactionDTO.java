package de.se.cashregistersystem.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemTransactionDTO {
    private String id;
    private String transactionRecordId;
    private String itemId;

    // Optional: Konstruktor ohne ID für die Erstellung neuer ItemTransactions
    public ItemTransactionDTO(String transactionRecordId, String itemId) {
        this.transactionRecordId = transactionRecordId;
        this.itemId = itemId;
    }
}