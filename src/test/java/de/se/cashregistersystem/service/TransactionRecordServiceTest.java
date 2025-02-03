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

    @Mock
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void cancel_withValidTransactionId_deletesTransactionRecord() {
        UUID transactionId = UUID.randomUUID();
        List<Pledge> pledges = List.of(new Pledge());

        when(pledgeRepository.findPledgesByTransactionId(transactionId)).thenReturn(Optional.of(pledges));

        transactionRecordService.cancel(transactionId);

        verify(pledgeRepository).save(any(Pledge.class));
        verify(productTransactionRepository).deleteByTransactionRecordId(transactionId);
        verify(transactionRecordRepository).deleteById(transactionId);
    }

    @Test
    void cancel_withInvalidTransactionId_throwsNotFound() {
        UUID transactionId = UUID.randomUUID();

        when(pledgeRepository.findPledgesByTransactionId(transactionId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transactionRecordService.cancel(transactionId));

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
        Product product = new Product();
        product.setId(productIds.get(0));
        String receiptBarcodeId = "receiptBarcodeId";

        when(paymentService.processPayment("validOrderId")).thenReturn(transactionId);
        when(productTransactionRepository.getProductsByTransactionId(transactionId)).thenReturn(Optional.of(productIds));
        when(pledgeRepository.findPledgesByTransactionId(transactionId)).thenReturn(Optional.of(pledges));
        when(productRepository.findById(productIds.get(0))).thenReturn(Optional.of(product));
        when(printingService.printReceipt(anyList(), anyList())).thenReturn(receiptBarcodeId);
        when(transactionRecordRepository.findById(transactionId)).thenReturn(Optional.of(new TransactionRecord()));

        transactionRecordService.completeTransaction(body);

        verify(transactionRecordRepository).findById(transactionId);
        verify(transactionRecordRepository).save(any(TransactionRecord.class));
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

        when(paymentService.processPayment("validOrderId")).thenReturn(transactionId);
        when(productTransactionRepository.getProductsByTransactionId(transactionId)).thenReturn(Optional.empty());
        when(pledgeRepository.findPledgesByTransactionId(transactionId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transactionRecordService.completeTransaction(body));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No items found for transaction: " + transactionId, exception.getReason());
    }

    @Test
    void completeTransaction_withInvalidProductId_throwsBadRequest() {
        CompleteTransactionDTO body = new CompleteTransactionDTO();
        body.setOrderId("validOrderId");
        UUID transactionId = UUID.randomUUID();
        List<UUID> productIds = List.of(UUID.randomUUID());
        List<Pledge> pledges = List.of(new Pledge());

        when(paymentService.processPayment("validOrderId")).thenReturn(transactionId);
        when(productTransactionRepository.getProductsByTransactionId(transactionId)).thenReturn(Optional.of(productIds));
        when(pledgeRepository.findPledgesByTransactionId(transactionId)).thenReturn(Optional.of(pledges));
        when(productRepository.findById(productIds.get(0))).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> transactionRecordService.completeTransaction(body));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Could not find product with id: Optional.empty", exception.getReason());
    }
    @Test
    void getTransactionRecord_withValidTransactionId_returnsTransactionRecord() {
        UUID transactionId = UUID.randomUUID();
        TransactionRecord transactionRecord = new TransactionRecord();

        when(transactionRecordRepository.findById(transactionId)).thenReturn(Optional.of(transactionRecord));

        TransactionRecord result = transactionRecordService.getTransactionRecord(transactionId);

        assertNotNull(result);
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
    void createTransactionRecord_withValidProductsAndPledges_returnsTransactionId() {
        ProductWithQuantityDTO[] products = { new ProductWithQuantityDTO(UUID.randomUUID(), 2) };
        UUID[] pledges = { UUID.randomUUID() };
        TransactionRecord transactionRecord = new TransactionRecord();
        transactionRecord.setId(UUID.randomUUID());
        Product product = new Product();
        product.setId(UUID.randomUUID());
        Pledge pledge = new Pledge();
        pledge.setId(UUID.randomUUID());

        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.of(product));
        when(pledgeRepository.findById(any(UUID.class))).thenReturn(Optional.of(pledge));
        when(transactionRecordFactory.create(anyList(), anyList())).thenReturn(transactionRecord);
        when(transactionRecordRepository.save(transactionRecord)).thenReturn(transactionRecord);
        when(productTransactionFactory.create(any(UUID.class), any(UUID.class))).thenReturn(new ProductTransaction());
        when(productTransactionRepository.save(any(ProductTransaction.class))).thenReturn(new ProductTransaction());

        UUID result = transactionRecordService.createTransactionRecord(products, pledges);

        assertNotNull(result);
        assertEquals(transactionRecord.getId(), result);
        verify(pledgeRepository, times(1)).save(any(Pledge.class));
        verify(productTransactionRepository, times(2)).save(any(ProductTransaction.class));
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
    void createTransactionRecord_withInvalidProductId_throwsBadRequest() {
        ProductWithQuantityDTO[] products = { new ProductWithQuantityDTO(UUID.randomUUID(), 1) };
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
    @Test
    void scan_withValidBarcodeId_updatesTransactionRecordStatus() {
        String barcodeId = "validBarcodeId";
        TransactionRecord transactionRecord = new TransactionRecord();
        transactionRecord.setStatus("paid");

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
}