package de.se.cashregistersystem.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRecordDTO {
    private String id;
    private Double totalAmount;

    // Optional: Konstruktor ohne ID für die Erstellung neuer TransactionRecords
    public TransactionRecordDTO(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}