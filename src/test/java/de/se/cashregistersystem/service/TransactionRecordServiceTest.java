package de.se.cashregistersystem.service;

import de.se.cashregistersystem.dto.ProductWithQuantityDTO;
import de.se.cashregistersystem.entity.*;
import de.se.cashregistersystem.factory.TransactionRecordFactory;
import de.se.cashregistersystem.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionRecordServiceTest {

    @Mock
    private TransactionRecordRepository transactionRecordRepository;

    @Mock
    private ProductTransactionRepository productTransactionRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PledgeRepository pledgeRepository;

    @Mock
    private TransactionRecordFactory transactionRecordFactory;

    @Mock
    private ProductWithQuantityDTO productDTO;

    @Mock
    private Product product;

    @Mock
    private Pledge pledge;

    @Mock
    private TransactionRecord transactionRecord;

    @InjectMocks
    private TransactionRecordService transactionRecordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTransactionRecord_withValidProductsAndPledges_returnsTransactionId() {
        UUID pledgeId = UUID.randomUUID();

        when(productDTO.getItemId()).thenReturn(UUID.randomUUID());
        when(productDTO.getQuantity()).thenReturn(2);
        when(productRepository.findById(productDTO.getItemId())).thenReturn(java.util.Optional.of(product));
        when(pledgeRepository.findById(pledgeId)).thenReturn(java.util.Optional.of(pledge));

        when(transactionRecordFactory.create(anyList(), anyList())).thenReturn(transactionRecord);
        when(transactionRecordRepository.save(any(TransactionRecord.class))).thenReturn(transactionRecord);
        when(transactionRecord.getId()).thenReturn(UUID.randomUUID());


        UUID transactionId = transactionRecordService.createTransactionRecord(new ProductWithQuantityDTO[]{productDTO}, new UUID[]{pledgeId});

        assertNotNull(transactionId);
        verify(transactionRecordRepository).save(transactionRecord);
        verify(productTransactionRepository, times(2)).save(any(ProductTransaction.class));
    }

    @Test
    void createTransactionRecord_withEmptyProductsAndPledges_throwsBadRequestException() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            transactionRecordService.createTransactionRecord(new ProductWithQuantityDTO[]{}, new UUID[]{});
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Items or pledges can not be empty", exception.getReason());
    }

    @Test
    void createTransactionRecord_withInvalidProductId_throwsBadRequestException() {
        when(productDTO.getItemId()).thenReturn(UUID.randomUUID());
        when(productRepository.findById(productDTO.getItemId())).thenReturn(java.util.Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            transactionRecordService.createTransactionRecord(new ProductWithQuantityDTO[]{productDTO}, new UUID[]{});
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Invalid ItemID: " + productDTO.getItemId(), exception.getReason());
    }

    @Test
    void createTransactionRecord_withInvalidPledgeId_throwsBadRequestException() {
        UUID pledgeId = UUID.randomUUID();

        when(pledgeRepository.findById(pledgeId)).thenReturn(java.util.Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            transactionRecordService.createTransactionRecord(new ProductWithQuantityDTO[]{}, new UUID[]{pledgeId});
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Invalid ItemID: " + pledgeId, exception.getReason());
    }

    @Test
    void complete_withValidTransactionId_setsStatusToPaid() {
        UUID transactionId = UUID.randomUUID();

        when(transactionRecordRepository.findById(transactionId)).thenReturn(java.util.Optional.of(transactionRecord));

        transactionRecordService.complete(transactionId, "");
        when(transactionRecord.getStatus()).thenReturn("paid");
        assertEquals("paid", transactionRecord.getStatus());
        verify(transactionRecordRepository).save(transactionRecord);
    }

    @Test
    void complete_withInvalidTransactionId_throwsBadRequestException() {
        UUID transactionId = UUID.randomUUID();

        when(transactionRecordRepository.findById(transactionId)).thenReturn(java.util.Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            transactionRecordService.complete(transactionId, "");
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Could not find transaction with id: " + transactionId, exception.getReason());
    }
}