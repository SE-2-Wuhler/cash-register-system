package de.se.cashregistersystem.factory;

import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.entity.Product;
import de.se.cashregistersystem.entity.TransactionRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TransactionRecordFactoryTest {

    @InjectMocks
    private TransactionRecordFactory transactionRecordFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_withValidProductsAndPledges_returnsTransactionRecord() {
        Product product1 = mock(Product.class);
        when(product1.getPrice()).thenReturn(1.0);
        when(product1.getPledgeValue()).thenReturn(0.25);

        Product product2 = mock(Product.class);
        when(product2.getPrice()).thenReturn(1.0);
        when(product2.getPledgeValue()).thenReturn(0.25);

        Pledge pledge1 = mock(Pledge.class);
        when(pledge1.getValue()).thenReturn(1.0);

        Pledge pledge2 = mock(Pledge.class);
        when(pledge2.getValue()).thenReturn(1.0);

        TransactionRecord transactionRecord = transactionRecordFactory.create(
                List.of(product1, product2), List.of(pledge1, pledge2));

        assertEquals(BigDecimal.valueOf(0.5), transactionRecord.getTotalAmount());
    }

    @Test
    void create_withNoProducts_returnsTransactionRecordWithNegativePledgeAmount() {
        Pledge pledge1 = mock(Pledge.class);
        when(pledge1.getValue()).thenReturn(15.0);

        Pledge pledge2 = mock(Pledge.class);
        when(pledge2.getValue()).thenReturn(25.0);

        TransactionRecord transactionRecord = transactionRecordFactory.create(
                List.of(), List.of(pledge1, pledge2));

        assertEquals(BigDecimal.valueOf(-40.0), transactionRecord.getTotalAmount());
    }

    @Test
    void create_withNoPledges_returnsTransactionRecordWithProductAmount() {
        Product product1 = mock(Product.class);
        when(product1.getPrice()).thenReturn(100.0);
        when(product1.getPledgeValue()).thenReturn(10.0);

        Product product2 = mock(Product.class);
        when(product2.getPrice()).thenReturn(200.0);
        when(product2.getPledgeValue()).thenReturn(20.0);

        TransactionRecord transactionRecord = transactionRecordFactory.create(
                List.of(product1, product2), List.of());

        assertEquals(BigDecimal.valueOf(330.0), transactionRecord.getTotalAmount());
    }

    @Test
    void create_withEmptyProductsAndPledges_returnsTransactionRecordWithZeroAmount() {
        TransactionRecord transactionRecord = transactionRecordFactory.create(
                List.of(), List.of());

        assertEquals(BigDecimal.valueOf(0.0), transactionRecord.getTotalAmount());
    }
}
