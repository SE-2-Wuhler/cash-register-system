package de.se.cashregistersystem.entity;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "transaction_record")
public class TransactionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "totalAmount", nullable = false, precision = 10, scale = 2)
    private Double totalAmount;
}
