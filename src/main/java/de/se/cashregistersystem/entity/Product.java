package de.se.cashregistersystem.entity;


import de.se.cashregistersystem.util.Scanable;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "item")
public class Product implements Scanable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;


    @Column(name = "barcode_id")
    private String barcodeId;

    public UUID getBrandId() {
        return brandId;
    }

    public void setBrandId(UUID brandId) {
        this.brandId = brandId;
    }

    @Column(name="brand_id")
    private UUID brandId;
//    @ManyToOne
//    @JoinColumn(name = "brand_id")
//    private Brand brand;


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



    public void setCategory(String category) {
        this.category = category;
    }

    // Default constructor
    public Product() {}

    // Constructor with all attributes
    public Product(String name, String barcodeId, UUID brandId, String description,
                   char nutriscore, String imgUrl, double price, double pledgeValue,
                   boolean isNonScanable, String category) {
        this.name = name;
        this.barcodeId = barcodeId;
        this.brandId = brandId;
        this.description = description;
        this.nutriscore = nutriscore;
        this.imgUrl = imgUrl;
        this.price = price;
        this.pledgeValue = pledgeValue;
        this.isNonScanable = isNonScanable;
        this.category = category;
    }

    public UUID getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcodeId() {
        return barcodeId;
    }

    public void setBarcodeId(String barcodeId) {
        this.barcodeId = barcodeId;
    }





    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public char getNutriscore() {
        return nutriscore;
    }

    public void setNutriscore(char nutriscore) {
        this.nutriscore = nutriscore;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public boolean isPledge() {
        return false;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPledgeValue() {
        return pledgeValue;
    }

    public void setPledgeValue(double pledgeValue) {
        this.pledgeValue = pledgeValue;
    }

    public boolean isNonScanable() {
        return isNonScanable;
    }

    public void setNonScanable(boolean nonScanable) {
        isNonScanable = nonScanable;
    }

    public String getCategory() {
        return category;
    }

}