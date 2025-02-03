
package de.se.cashregistersystem.controller;

import de.se.cashregistersystem.entity.Product;
import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.repository.ProductRepository;
import de.se.cashregistersystem.repository.PledgeRepository;
import de.se.cashregistersystem.dto.Scanable;
import de.se.cashregistersystem.service.ItemService;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemControllerTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PledgeRepository pledgeRepository;

    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getById_withValidProductBarcode_returnsProduct() {
        String barcodeId = "validProductBarcode";
        Product product = new Product();
        when(itemService.getById(barcodeId)).thenReturn(product);

        ResponseEntity<Scanable> response = itemController.getById(barcodeId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(product, response.getBody());
    }

    @Test
    void getById_withValidPledgeBarcode_returnsPledge() {
        String barcodeId = "validPledgeBarcode";
        Pledge pledge = new Pledge();
        when(itemService.getById(barcodeId)).thenReturn(pledge);

        ResponseEntity<Scanable> response = itemController.getById(barcodeId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pledge, response.getBody());
    }

    @Test
    void getById_withInvalidBarcode_throwsBadRequest() {
        String barcodeId = " ";
        when(itemService.getById(barcodeId)).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Barcode cannot be null or empty"));
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> itemController.getById(barcodeId));
        ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.BAD_REQUEST, "Barcode cannot be null or empty");
        assertEquals(responseStatusException.getMessage(), exception.getMessage());
    }

    @Test
    void getById_withNonExistentBarcode_throwsNotFound() {
        String barcodeId = "nonExistentBarcode";
        when(itemService.getById(barcodeId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No product or valid pledge found with barcode: nonExistentBarcode"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> itemController.getById(barcodeId));
        ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.NOT_FOUND, "No product or valid pledge found with barcode: nonExistentBarcode");
        assertEquals(responseStatusException.getMessage(), exception.getMessage());
    }

    @Test
    void getById_withValidatedPledge_throwsNotFound() {
        String barcodeId = "validatedPledgeBarcode";
        Pledge pledge = mock(Pledge.class);
        when(pledge.isValidated()).thenReturn(true);
        when(itemService.getById(barcodeId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No product or valid pledge found with barcode: " + barcodeId));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> itemController.getById(barcodeId));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No product or valid pledge found with barcode: " + barcodeId, exception.getReason());
    }

    @Test
    void getById_withNonScanableItems_returnsItems() {
        List<Product> products = List.of(new Product(), new Product());
        when(itemService.getAllNonScanableProducts()).thenReturn(products);

        ResponseEntity<List<Product>> response = itemController.getById();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(products, response.getBody());
    }

    @Test
    void getById_withNoNonScanableItems_throwsNotFound() {
        when(itemService.getAllNonScanableProducts()).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Did not find nonscanable items"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> itemController.getById());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Did not find nonscanable items", exception.getReason());
    }
}
