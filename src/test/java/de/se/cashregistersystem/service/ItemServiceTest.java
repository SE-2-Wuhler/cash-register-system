package de.se.cashregistersystem.service;

import de.se.cashregistersystem.dto.CreateProductDTO;
import de.se.cashregistersystem.entity.*;
import de.se.cashregistersystem.dto.Scanable;
import de.se.cashregistersystem.factory.ProductFactory;
import de.se.cashregistersystem.repository.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PledgeRepository pledgeRepository;

    @InjectMocks
    private ItemService itemService;

    @Test
    void getById_withValidProductBarcode_returnsProduct() {
        String barcodeId = "validBarcode";
        Product product = new Product();
        when(productRepository.findProductByBarcodeId(barcodeId)).thenReturn(Optional.of(product));

        Scanable result = itemService.getById(barcodeId);

        assertEquals(product, result);
    }

    @Test
    void getById_withValidPledgeBarcode_returnsPledge() {
        String barcodeId = "validPledgeBarcode";
        Pledge pledge = new Pledge();
        when(productRepository.findProductByBarcodeId(barcodeId)).thenReturn(Optional.empty());
        when(pledgeRepository.findPledgeByBarcodeId(barcodeId)).thenReturn(Optional.of(pledge));

        Scanable result = itemService.getById(barcodeId);

        assertEquals(pledge, result);
    }

    @Test
    void getById_withNullBarcode_throwsBadRequest() {
        String barcodeId = null;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> itemService.getById(barcodeId));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Barcode cannot be null or empty", exception.getReason());
    }

    @Test
    void getById_withEmptyBarcode_throwsBadRequest() {
        String barcodeId = "";

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> itemService.getById(barcodeId));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Barcode cannot be null or empty", exception.getReason());
    }

    @Test
    void getById_withNonExistentBarcode_throwsNotFound() {
        String barcodeId = "nonExistentBarcode";
        when(productRepository.findProductByBarcodeId(barcodeId)).thenReturn(Optional.empty());
        when(pledgeRepository.findPledgeByBarcodeId(barcodeId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> itemService.getById(barcodeId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No product or valid pledge found with barcode: " + barcodeId, exception.getReason());
    }

    @Test
    void getAllNonScanableProducts_withNonScanableProducts_returnsProducts() {
        List<Product> products = List.of(new Product(), new Product());
        when(productRepository.findAllByIsNonScanableTrue()).thenReturn(Optional.of(products));

        List<Product> result = itemService.getAllNonScanableProducts();

        assertEquals(products, result);
    }

    @Test
    void getAllNonScanableProducts_withNoNonScanableProducts_throwsNotFound() {
        when(productRepository.findAllByIsNonScanableTrue()).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> itemService.getAllNonScanableProducts());

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Did not find nonscanable items", exception.getReason());
    }
}
