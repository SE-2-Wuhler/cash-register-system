package de.se.cashregistersystem.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "transaction_record")
public class TransactionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "totalAmount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
}
