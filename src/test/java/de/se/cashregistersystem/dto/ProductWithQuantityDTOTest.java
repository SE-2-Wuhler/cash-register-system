package de.se.cashregistersystem.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;



public class ProductWithQuantityDTOTest {

    @Test
    public void testDefaultConstructor() {
        ProductWithQuantityDTO dto = new ProductWithQuantityDTO();
        assertNotNull(dto);
    }

    @Test
    public void testConstructorWithQuantity() {
        int quantity = 5;
        ProductWithQuantityDTO dto = new ProductWithQuantityDTO(quantity);
        assertEquals(quantity, dto.getQuantity());
    }

    @Test
    public void testGetItemId() {
        ProductWithQuantityDTO dto = new ProductWithQuantityDTO();
        assertNull(dto.getItemId());
    }
}