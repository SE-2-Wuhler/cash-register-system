package de.se.cashregistersystem.controller;

import de.se.cashregistersystem.dto.CompleteOrderResponseDTO;
import de.se.cashregistersystem.dto.CompleteTransactionDTO;
import de.se.cashregistersystem.dto.TransactionRequestDTO;
import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.entity.Product;
import de.se.cashregistersystem.entity.TransactionRecord;
import de.se.cashregistersystem.repository.*;
import de.se.cashregistersystem.service.PayPalService;
import de.se.cashregistersystem.service.PrintingService;
import de.se.cashregistersystem.service.TransactionRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionRecordControllerTest {

    @Mock
    private TransactionRecordRepository transactionRecordRepository;

    @Mock
    private ProductTransactionRepository productTransactionRepository;

    @Mock
    private TransactionRecordService service;

    @Mock
    private PayPalService paypalService;

    @Mock
    private PrintingService printingService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PledgeRepository pledgeRepository;

    @InjectMocks
    private TransactionRecordController transactionRecordController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTransactionById_withValidId_returnsTransaction() {
        UUID id = UUID.randomUUID();
        TransactionRecord transaction = new TransactionRecord();
        when(service.getTransactionRecord(id)).thenReturn(transaction);

        ResponseEntity<TransactionRecord> response = transactionRecordController.getTransactionById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transaction, response.getBody());
    }

    @Test
    void getTransactionById_withInvalidId_throwsNotFound() {
        UUID id = UUID.randomUUID();
        when(service.getTransactionRecord(id)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found."));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transactionRecordController.getTransactionById(id));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Transaction not found.", exception.getReason());
    }

    @Test
    void create_withValidRequest_createsTransaction() {
        TransactionRequestDTO requestDTO = mock(TransactionRequestDTO.class);
        UUID transactionId = UUID.randomUUID();
        when(service.createTransactionRecord(requestDTO.getItems(), requestDTO.getPledges())).thenReturn(transactionId);

        ResponseEntity<Object> response = transactionRecordController.create(requestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(transactionId, response.getBody());
    }

    @Test
    void completeTransaction_withValidOrderId_completesTransaction() {
        CompleteTransactionDTO body = mock(CompleteTransactionDTO.class);
        when(body.getOrderId()).thenReturn("validOrderId");
        doNothing().when(service).completeTransaction(body);

        ResponseEntity<CompleteOrderResponseDTO> response = transactionRecordController.completeTransaction(body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Transaction completed", response.getBody().getMessage());
    }

    @Test
    void completeTransaction_withInvalidOrderId_throwsBadRequest() {
        CompleteTransactionDTO body = mock(CompleteTransactionDTO.class);
        when(body.getOrderId()).thenReturn(" ");
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order ID cannot be null or empty")).when(service).completeTransaction(body);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transactionRecordController.completeTransaction(body));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Order ID cannot be null or empty", exception.getReason());
    }

    @Test
    void completeTransaction_withNoItemsFound_throwsNotFound() {
        CompleteTransactionDTO body = mock(CompleteTransactionDTO.class);
        when(body.getOrderId()).thenReturn("validOrderId");
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No items found for transaction")).when(service).completeTransaction(body);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transactionRecordController.completeTransaction(body));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No items found for transaction", exception.getReason());
    }

    @Test
    void cancel_withValidId_cancelsTransaction() {
        UUID id = UUID.randomUUID();
        doNothing().when(service).cancel(id);

        ResponseEntity<CompleteOrderResponseDTO> response = transactionRecordController.cancel(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Transaction cancelled", response.getBody().getMessage());
    }

    @Test
    void cancel_withInvalidId_throwsNotFound() {
        UUID id = UUID.randomUUID();
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found")).when(service).cancel(id);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transactionRecordController.cancel(id));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Transaction not found", exception.getReason());
    }

    @Test
    void scanTransaction_withValidBarcode_scansTransaction() {
        String barcodeId = "validBarcode";
        doNothing().when(service).scan(barcodeId);

        ResponseEntity<Object> response = transactionRecordController.scanTransaction(barcodeId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Transaction successfully scanned", ((CompleteOrderResponseDTO) response.getBody()).getMessage());
    }

    @Test
    void scanTransaction_withInvalidBarcode_throwsNotFound() {
        String barcodeId = "invalidBarcode";
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No paid Transaction Record found")).when(service).scan(barcodeId);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transactionRecordController.scanTransaction(barcodeId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No paid Transaction Record found", exception.getReason());
    }
}