package de.se.cashregistersystem.entity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class ProductTransactionTest {

    @Test
    public void testConstructor() {
        UUID transactionRecordId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        ProductTransaction productTransaction = new ProductTransaction(transactionRecordId, productId);

        assertEquals(transactionRecordId, productTransaction.getTransactionRecordId());
        assertEquals(productId, productTransaction.getProductId());
    }

    @Test
    public void testSetTransactionRecordId() {
        ProductTransaction productTransaction = new ProductTransaction(UUID.randomUUID(), UUID.randomUUID());
        UUID newTransactionRecordId = UUID.randomUUID();
        productTransaction.setTransactionRecordId(newTransactionRecordId);

        assertEquals(newTransactionRecordId, productTransaction.getTransactionRecordId());
    }

    @Test
    public void testSetProductId() {
        ProductTransaction productTransaction = new ProductTransaction(UUID.randomUUID(), UUID.randomUUID());
        UUID newProductId = UUID.randomUUID();
        productTransaction.setProductId(newProductId);

        assertEquals(newProductId, productTransaction.getProductId());
    }
}