package de.se.cashregistersystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "item")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter
    private UUID id;

    @Setter
    @Getter
    @Column(name = "name", nullable = false)
    private String name;

    @Setter
    @Getter
    @Column(name = "barcode_id")
    private String barcodeId;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Setter
    @Getter
    @Column(name = "description")
    private String description;

    @Setter
    @Getter
    @Column(name = "nutriscore")
    private char nutriscore;

    @Setter
    @Getter
    @Column(name = "img_url")
    private String imgUrl;

    @Setter
    @Getter
    @Column(name = "price", nullable = false)
    private double price;

    @Setter
    @Getter
    @Column(name = "pledgevalue")
    private double pledgeValue;

    @Setter
    @Getter
    @Column(name = "is_non_scanable")
    private boolean isNonScanable;

    @Setter
    @Getter
    @Column(name = "category")
    private String category;

// Getters and setters
}