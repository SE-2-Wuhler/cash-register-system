package de.se.cashregistersystem.entity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;





public class PledgeTest {

    @Test
    public void testPledgeConstructorWithValue() {
        double value = 10.0;
        Pledge pledge = new Pledge(value);
        assertEquals(value, pledge.getValue());
    }

    @Test
    public void testSetAndGetBarcodeId() {
        Pledge pledge = new Pledge();
        String barcodeId = "123456789";
        pledge.setBarcodeId(barcodeId);
        assertEquals(barcodeId, pledge.getBarcodeId());
    }

    @Test
    public void testSetAndGetValue() {
        Pledge pledge = new Pledge();
        double value = 20.0;
        pledge.setValue(value);
        assertEquals(value, pledge.getValue());
    }

    @Test
    public void testSetAndGetTransactionId() {
        Pledge pledge = new Pledge();
        UUID transactionId = UUID.randomUUID();
        pledge.setTransactionId(transactionId);
        assertEquals(transactionId, pledge.getTransactionId());
    }

    @Test
    public void testIsValidated() {
        Pledge pledge = new Pledge();
        assertFalse(pledge.isValidated());
        UUID transactionId = UUID.randomUUID();
        pledge.setTransactionId(transactionId);
        assertTrue(pledge.isValidated());
    }

    @Test
    public void testIsPledge() {
        Pledge pledge = new Pledge();
        assertTrue(pledge.isPledge());
    }

    @Test
    public void testGetId() {
        Pledge record = new Pledge();
        assertEquals(record.getId(), null);
    }
}