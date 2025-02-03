package de.se.cashregistersystem.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;



public class BrandTest {

    @Test
    public void testBrandConstructor() {
        Brand brand = new Brand("TestName", "TestAddress");
        assertEquals("TestName", brand.getName());
        assertEquals("TestAddress", brand.getAddress());
    }

    @Test
    public void testSetName() {
        Brand brand = new Brand();
        brand.setName("NewName");
        assertEquals("NewName", brand.getName());
    }

    @Test
    public void testSetAddress() {
        Brand brand = new Brand();
        brand.setAddress("NewAddress");
        assertEquals("NewAddress", brand.getAddress());
    }

    @Test
    public void testGetId() {
        Brand brand = new Brand();
        assertNull(brand.getId()); // ID should be null before persistence
    }

    @Test
    public void testDefaultConstructor() {
        Brand brand = new Brand();
        assertNull(brand.getName());
        assertNull(brand.getAddress());
    }

    @Test
    public void testParameterizedConstructor() {
        Brand brand = new Brand("BrandName", "BrandAddress");
        assertEquals("BrandName", brand.getName());
        assertEquals("BrandAddress", brand.getAddress());
    }
}