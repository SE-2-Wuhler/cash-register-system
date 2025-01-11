package de.se.cashregistersystem.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {
    private String id;
    private String name;
    private String barcodeId;
    private UUID brandId;
    private String description;
    private char nutriscore;
    private String imgUrl;
    private double price;
    private double pledgeValue;
    private boolean isNonScanable;

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

    public UUID getBrandId() {
        return brandId;
    }

    public void setBrandId(UUID brandId) {
        this.brandId = brandId;
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

    public void setCategory(String category) {
        this.category = category;
    }

    private String category;


    public ItemDTO(String name, String barcodeId, UUID brandId, String description,
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
}