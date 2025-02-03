package de.se.cashregistersystem.dto;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import org.junit.jupiter.api.Test;



public class TransactionRequestDTOTest {

    @Test
    public void testGetItems() {
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();
        ProductWithQuantityDTO[] items = new ProductWithQuantityDTO[2];
        items[0] = new ProductWithQuantityDTO();
        items[1] = new ProductWithQuantityDTO();
        transactionRequestDTO.items = items;

        assertArrayEquals(items, transactionRequestDTO.getItems());
    }

    @Test
    public void testGetPledges() {
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();
        UUID[] pledges = new UUID[2];
        pledges[0] = UUID.randomUUID();
        pledges[1] = UUID.randomUUID();
        transactionRequestDTO.pledges = pledges;

        assertArrayEquals(pledges, transactionRequestDTO.getPledges());
    }
}