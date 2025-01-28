package de.se.cashregistersystem.entity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;



public class ProductTest {

    @Test
    public void testDefaultConstructor() {
        Product product = new Product();
        assertNull(product.getId());
        assertNull(product.getName());
        assertNull(product.getBarcodeId());
        assertNull(product.getBrandId());
        assertNull(product.getDescription());
        assertEquals('\0', product.getNutriscore());
        assertNull(product.getImgUrl());
        assertEquals(0.0, product.getPrice());
        assertEquals(0.0, product.getPledgeValue());
        assertFalse(product.isNonScanable());
        assertNull(product.getCategory());
    }

    @Test
    public void testConstructorWithAllAttributes() {
        UUID brandId = UUID.randomUUID();
        Product product = new Product("Test Product", "123456789", brandId, "Test Description",
                'A', "http://example.com/image.jpg", 9.99, 0.5, true, "Test Category");

        assertEquals("Test Product", product.getName());
        assertEquals("123456789", product.getBarcodeId());
        assertEquals(brandId, product.getBrandId());
        assertEquals("Test Description", product.getDescription());
        assertEquals('A', product.getNutriscore());
        assertEquals("http://example.com/image.jpg", product.getImgUrl());
        assertEquals(9.99, product.getPrice());
        assertEquals(0.5, product.getPledgeValue());
        assertTrue(product.isNonScanable());
        assertEquals("Test Category", product.getCategory());
    }

    @Test
    public void testSetName() {
        Product product = new Product();
        product.setName("New Name");
        assertEquals("New Name", product.getName());
    }

    @Test
    public void testSetBarcodeId() {
        Product product = new Product();
        product.setBarcodeId("987654321");
        assertEquals("987654321", product.getBarcodeId());
    }

    @Test
    public void testSetBrandId() {
        Product product = new Product();
        UUID brandId = UUID.randomUUID();
        product.setBrandId(brandId);
        assertEquals(brandId, product.getBrandId());
    }

    @Test
    public void testSetDescription() {
        Product product = new Product();
        product.setDescription("New Description");
        assertEquals("New Description", product.getDescription());
    }

    @Test
    public void testSetNutriscore() {
        Product product = new Product();
        product.setNutriscore('B');
        assertEquals('B', product.getNutriscore());
    }

    @Test
    public void testSetImgUrl() {
        Product product = new Product();
        product.setImgUrl("http://example.com/newimage.jpg");
        assertEquals("http://example.com/newimage.jpg", product.getImgUrl());
    }

    @Test
    public void testSetPrice() {
        Product product = new Product();
        product.setPrice(19.99);
        assertEquals(19.99, product.getPrice());
    }

    @Test
    public void testSetPledgeValue() {
        Product product = new Product();
        product.setPledgeValue(1.0);
        assertEquals(1.0, product.getPledgeValue());
    }

    @Test
    public void testSetNonScanable() {
        Product product = new Product();
        product.setNonScanable(true);
        assertTrue(product.isNonScanable());
    }

    @Test
    public void testSetCategory() {
        Product product = new Product();
        product.setCategory("New Category");
        assertEquals("New Category", product.getCategory());
    }

    @Test
    public void testIsPledge() {
        Product product = new Product();
        assertFalse(product.isPledge());
    }
}