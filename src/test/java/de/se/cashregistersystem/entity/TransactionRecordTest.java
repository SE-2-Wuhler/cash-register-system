package de.se.cashregistersystem.entity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

public class TransactionRecordTest {

    @Test
    public void testTransactionRecordConstructor() {
        BigDecimal amount = new BigDecimal("100.00");
        TransactionRecord record = new TransactionRecord(amount);
        assertEquals(amount, record.getTotalAmount());
    }

    @Test
    public void testSetTotalAmount() {
        TransactionRecord record = new TransactionRecord();
        BigDecimal amount = new BigDecimal("200.00");
        record.setTotalAmount(amount);
        assertEquals(amount, record.getTotalAmount());
    }

    @Test
    public void testSetStatus() {
        TransactionRecord record = new TransactionRecord();
        String status = "COMPLETED";
        record.setStatus(status);
        assertEquals(status, record.getStatus());
    }

    @Test
    public void testGetId() {
        TransactionRecord record = new TransactionRecord();
        assertEquals(record.getId(), null);
    }

    @Test
    public void testDefaultConstructor() {
        TransactionRecord record = new TransactionRecord();
        assertNull(record.getTotalAmount());
        assertEquals(record.getStatus(), "unpaid");
    }
}