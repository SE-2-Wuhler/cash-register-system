package de.se.cashregistersystem.dto;

import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;



public class CompleteTransactionDTOTest {

    @Test
    public void testGetOrderId() {
        CompleteTransactionDTO dto = new CompleteTransactionDTO();
        assertNull(dto.getOrderId(), "OrderId should be null initially");
    }

}