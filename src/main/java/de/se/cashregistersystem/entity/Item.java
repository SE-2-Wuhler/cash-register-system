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
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;


    @Column(name = "barcode_id")
    private String barcodeId;


    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;


    @Column(name = "description")
    private String description;


    @Column(name = "nutriscore")
    private char nutriscore;


    @Column(name = "img_url")
    private String imgUrl;


    @Column(name = "price", nullable = false)
    private double price;


    @Column(name = "pledgevalue")
    private double pledgeValue;


    @Column(name = "is_non_scanable")
    private boolean isNonScanable;


    @Column(name = "category")
    private String category;

// Getters and setters
}