package de.se.cashregistersystem.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class CreateProductDTOTest {

    @Test
    public void testConstructorAndGetters() {
        String barcodeId = "123456789";
        double price = 9.99;
        double pledgeValue = 1.50;

        CreateProductDTO product = new CreateProductDTO(barcodeId, price, pledgeValue);

        assertEquals(barcodeId, product.getBarcodeId());
        assertEquals(price, product.getPrice());
        assertEquals(pledgeValue, product.getPledgeValue());
    }

    @Test
    public void testSetPledgeValue() {
        String barcodeId = "123456789";
        double price = 9.99;
        double initialPledgeValue = 1.50;
        double newPledgeValue = 2.00;

        CreateProductDTO product = new CreateProductDTO(barcodeId, price, initialPledgeValue);
        product.setPledgeValue(newPledgeValue);

        assertEquals(newPledgeValue, product.getPledgeValue());
    }
}