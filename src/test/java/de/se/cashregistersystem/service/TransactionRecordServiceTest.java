package de.se.cashregistersystem.service;

import de.se.cashregistersystem.dto.CompleteTransactionDTO;
import de.se.cashregistersystem.dto.ProductWithQuantityDTO;
import de.se.cashregistersystem.entity.*;
import de.se.cashregistersystem.factory.ProductTransactionFactory;
import de.se.cashregistersystem.factory.TransactionRecordFactory;
import de.se.cashregistersystem.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
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
    private TransactionRecordFactory transactionRecordFactory;

    @Mock
    private ProductTransactionFactory productTransactionFactory;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PledgeRepository pledgeRepository;

    @Mock
    private PayPalService paypalService;

    @InjectMocks
    private TransactionRecordService transactionRecordService;

    @Mock
    private PrintingService printingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTransactionRecord_withValidTransactionId_returnsTransactionRecord() {
        UUID transactionId = UUID.randomUUID();
        TransactionRecord transactionRecord = new TransactionRecord();
        when(transactionRecordRepository.findById(transactionId)).thenReturn(Optional.of(transactionRecord));

        TransactionRecord result = transactionRecordService.getTransactionRecord(transactionId);

        assertEquals(transactionRecord, result);
    }

    @Test
    void getTransactionRecord_withInvalidTransactionId_throwsNotFound() {
        UUID transactionId = UUID.randomUUID();
        when(transactionRecordRepository.findById(transactionId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transactionRecordService.getTransactionRecord(transactionId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Transaction not found.", exception.getReason());
    }

    @Test
    void completeTransaction_withValidOrderId_completesTransaction() {
        CompleteTransactionDTO body = new CompleteTransactionDTO();
        body.setOrderId("validOrderId");
        UUID transactionId = UUID.randomUUID();
        List<UUID> productIds = List.of(UUID.randomUUID());
        List<Pledge> pledges = List.of(new Pledge());

        when(paypalService.verifyPayment("validOrderId")).thenReturn(transactionId);
        when(productTransactionRepository.getProductsByTransactionId(transactionId)).thenReturn(Optional.of(productIds));
        when(pledgeRepository.findPledgesByTransactionId(transactionId)).thenReturn(Optional.of(pledges));
        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.of(new Product()));
        when(transactionRecordRepository.findById(transactionId)).thenReturn(Optional.of(new TransactionRecord()));
        when(printingService.printReceipt(anyList(), anyList())).thenReturn("109238574");

        transactionRecordService.completeTransaction(body);

        verify(transactionRecordRepository).save(any(TransactionRecord.class));
        verify(printingService).printReceipt(anyList(), anyList());
    }

    @Test
    void completeTransaction_withNullOrderId_throwsBadRequest() {
        CompleteTransactionDTO body = new CompleteTransactionDTO();
        body.setOrderId(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transactionRecordService.completeTransaction(body));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Order ID cannot be null or empty", exception.getReason());
    }

    @Test
    void completeTransaction_withEmptyOrderId_throwsBadRequest() {
        CompleteTransactionDTO body = new CompleteTransactionDTO();
        body.setOrderId("");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transactionRecordService.completeTransaction(body));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Order ID cannot be null or empty", exception.getReason());
    }

    @Test
    void completeTransaction_withNoItemsFound_throwsNotFound() {
        CompleteTransactionDTO body = new CompleteTransactionDTO();
        body.setOrderId("validOrderId");
        UUID transactionId = UUID.randomUUID();

        when(paypalService.verifyPayment("validOrderId")).thenReturn(transactionId);
        when(productTransactionRepository.getProductsByTransactionId(transactionId)).thenReturn(Optional.empty());
        when(pledgeRepository.findPledgesByTransactionId(transactionId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transactionRecordService.completeTransaction(body));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No items found for transaction: " + transactionId, exception.getReason());
    }

    @Test
    void scan_withValidBarcodeId_setsStatusToScanned() {
        String barcodeId = "validBarcodeId";
        TransactionRecord transactionRecord = new TransactionRecord();

        when(transactionRecordRepository.findPaidTransactionRecord(barcodeId)).thenReturn(Optional.of(transactionRecord));

        transactionRecordService.scan(barcodeId);

        assertEquals("scanned", transactionRecord.getStatus());
        verify(transactionRecordRepository).save(transactionRecord);
    }

    @Test
    void scan_withInvalidBarcodeId_throwsNotFound() {
        String barcodeId = "invalidBarcodeId";

        when(transactionRecordRepository.findPaidTransactionRecord(barcodeId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transactionRecordService.scan(barcodeId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No paid Transaction Record", exception.getReason());
    }
    @Test
    void createTransactionRecord_withValidProductsAndPledges_createsTransactionRecord() {
        ProductWithQuantityDTO[] products = { new ProductWithQuantityDTO(UUID.randomUUID(), 2) };
        UUID[] pledges = { UUID.randomUUID() };
        List<Product> productList = List.of(new Product());
        List<Pledge> pledgeList = List.of(new Pledge());
        TransactionRecord transactionRecord = new TransactionRecord(UUID.randomUUID());

        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.of(new Product()));
        when(pledgeRepository.findById(any(UUID.class))).thenReturn(Optional.of(new Pledge()));
        when(transactionRecordFactory.create(anyList(), anyList())).thenReturn(transactionRecord);
        when(transactionRecordRepository.save(any(TransactionRecord.class))).thenReturn(transactionRecord);
        when(productTransactionFactory.create(any(UUID.class), any(UUID.class))).thenReturn(new ProductTransaction());

        UUID result = transactionRecordService.createTransactionRecord(products, pledges);

        assertNotNull(result);
        verify(transactionRecordRepository).save(transactionRecord);
    }

    @Test
    void createTransactionRecord_withEmptyProductsAndPledges_throwsBadRequest() {
        ProductWithQuantityDTO[] products = {};
        UUID[] pledges = {};

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transactionRecordService.createTransactionRecord(products, pledges));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Items or pledges can not be empty", exception.getReason());
    }

    @Test
    void createTransactionRecord_withNullProductsAndPledges_throwsBadRequest() {
        ProductWithQuantityDTO[] products = null;
        UUID[] pledges = null;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transactionRecordService.createTransactionRecord(products, pledges));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Items or pledges can not be empty", exception.getReason());
    }

    @Test
    void createTransactionRecord_withInvalidProductId_throwsBadRequest() {
        ProductWithQuantityDTO[] products = { new ProductWithQuantityDTO(UUID.randomUUID(), 2) };
        UUID[] pledges = {};

        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transactionRecordService.createTransactionRecord(products, pledges));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Invalid ItemID: " + products[0].getItemId(), exception.getReason());
    }

    @Test
    void createTransactionRecord_withInvalidPledgeId_throwsBadRequest() {
        ProductWithQuantityDTO[] products = {};
        UUID[] pledges = { UUID.randomUUID() };

        when(pledgeRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transactionRecordService.createTransactionRecord(products, pledges));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Invalid ItemID: " + pledges[0], exception.getReason());
    }
}