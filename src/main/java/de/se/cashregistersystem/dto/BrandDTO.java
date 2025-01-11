package de.se.cashregistersystem.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BrandDTO {
    private String id;
    private String name;
    private String address;

    // Optional: Konstruktor ohne ID für die Erstellung neuer Brands
    public BrandDTO(String name, String address) {
        this.name = name;
        this.address = address;
    }
}