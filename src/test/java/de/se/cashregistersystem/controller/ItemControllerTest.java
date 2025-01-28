
package de.se.cashregistersystem.controller;

import de.se.cashregistersystem.entity.Product;
import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.repository.ProductRepository;
import de.se.cashregistersystem.repository.PledgeRepository;
import de.se.cashregistersystem.dto.Scanable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemControllerTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PledgeRepository pledgeRepository;

    @InjectMocks
    private ItemController itemController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getById_withValidProductBarcode_returnsProduct() {
        String barcodeId = "validProductBarcode";
        Product product = new Product();
        when(productRepository.findItemByBarcodeId(barcodeId)).thenReturn(Optional.of(product));

        ResponseEntity<Scanable> response = itemController.getById(barcodeId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(product, response.getBody());
    }

    @Test
    void getById_withValidPledgeBarcode_returnsPledge() {
        String barcodeId = "validPledgeBarcode";
        Pledge pledge = new Pledge();
        when(productRepository.findItemByBarcodeId(barcodeId)).thenReturn(Optional.empty());
        when(pledgeRepository.findPledgeByBarcodeId(barcodeId)).thenReturn(Optional.of(pledge));

        ResponseEntity<Scanable> response = itemController.getById(barcodeId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pledge, response.getBody());
    }

    @Test
    void getById_withInvalidBarcode_throwsBadRequest() {
        String barcodeId = " ";
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> itemController.getById(barcodeId));
        ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.BAD_REQUEST, "Barcode cannot be null or empty");
        assertEquals(responseStatusException.getMessage(), exception.getMessage());
    }

    @Test
    void getById_withNonExistentBarcode_throwsNotFound() {
        String barcodeId = "nonExistentBarcode";
        when(productRepository.findItemByBarcodeId(barcodeId)).thenReturn(Optional.empty());
        when(pledgeRepository.findPledgeByBarcodeId(barcodeId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> itemController.getById(barcodeId));
        ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.NOT_FOUND, "No product or valid pledge found with barcode: nonExistentBarcode");
        assertEquals(responseStatusException.getMessage(), exception.getMessage());
    }

    @Test
    void getById_withValidatedPledge_throwsNotFound() {
        String barcodeId = "validatedPledgeBarcode";
        Pledge pledge = mock(Pledge.class);
        when(productRepository.findItemByBarcodeId(barcodeId)).thenReturn(Optional.empty());
        when(pledgeRepository.findPledgeByBarcodeId(barcodeId)).thenReturn(Optional.of(pledge));

        when(pledge.isValidated()).thenReturn(true);
        when(productRepository.findItemByBarcodeId(barcodeId)).thenReturn(Optional.empty());
        when(pledgeRepository.findPledgeByBarcodeId(barcodeId)).thenReturn(Optional.of(pledge));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> itemController.getById(barcodeId));
        ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.NOT_FOUND, "No product or valid pledge found with barcode: validatedPledgeBarcode");
        assertEquals(responseStatusException.getMessage(), exception.getMessage());
    }

    @Test
    void getById_withNonScanableItems_returnsItems() {
        List<Product> products = List.of(new Product(), new Product());
        when(productRepository.findAllByIsNonScanableTrue()).thenReturn(Optional.of(products));

        ResponseEntity<List<Product>> response = itemController.getById();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(products, response.getBody());
    }

    @Test
    void getById_withNoNonScanableItems_throwsNotFound() {
        when(productRepository.findAllByIsNonScanableTrue()).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> itemController.getById());
        ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.NOT_FOUND, "Did not find nonscanable items");

        assertEquals(responseStatusException.getMessage(), exception.getMessage());

    }
}
