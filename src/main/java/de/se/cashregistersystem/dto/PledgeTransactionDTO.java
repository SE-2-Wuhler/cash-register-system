package de.se.cashregistersystem.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PledgeTransactionDTO {
    private String id;
    private String transactionId;
    private String pledgeId;

    // Optional: Konstruktor ohne ID für die Erstellung neuer PledgeTransactions
    public PledgeTransactionDTO(String transactionId, String pledgeId) {
        this.transactionId = transactionId;
        this.pledgeId = pledgeId;
    }
}