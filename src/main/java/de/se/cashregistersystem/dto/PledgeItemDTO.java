package de.se.cashregistersystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PledgeItemDTO {
    private String id;
    private String name;
    private String barcodeId;
    private String imgUrl;
    private double pledgeValue;

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


    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }


    public double getPledgeValue() {
        return pledgeValue;
    }

    public void setPledgeValue(double pledgeValue) {
        this.pledgeValue = pledgeValue;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    private String category;


    public PledgeItemDTO(String name, String barcodeId, String imgUrl, double pledgeValue, String category) {
        this.name = name;
        this.barcodeId = barcodeId;
        this.imgUrl = imgUrl;
        this.pledgeValue = pledgeValue;
        this.category = category;
    }
}