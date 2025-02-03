package de.se.cashregistersystem.factory;

import de.se.cashregistersystem.entity.Brand;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BrandFactoryTest {
    @Test
    void create_withValidName_returnsBrand() {
        String name = "BrandName";
        BrandFactory brandFactory = new BrandFactory();

        Brand result = brandFactory.create(name);

        assertNotNull(result);
        assertEquals(name, result.getName());
    }
}
