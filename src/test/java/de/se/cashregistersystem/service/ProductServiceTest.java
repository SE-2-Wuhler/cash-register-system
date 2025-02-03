package de.se.cashregistersystem.service;

import de.se.cashregistersystem.dto.CreateProductDTO;
import de.se.cashregistersystem.entity.*;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private OpenFoodFactsService foodService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductFactory productFactory;

    @InjectMocks
    private ProductService productService;


    @Test
    void create_withValidRequest_createsProduct() {
        CreateProductDTO request = mock(CreateProductDTO.class);
        when(request.getBarcodeId()).thenReturn("validBarcode");
        when(request.getPrice()).thenReturn(10.0);
        JSONObject foodFacts = new JSONObject();
        try {
            foodFacts.put("brands", "BrandName");
            foodFacts.put("product_name", "ProductName");
            foodFacts.put("categories", "Category");
            foodFacts.put("nutriscore_grade", "A");
            foodFacts.put("image_front_small_url", "http://image.url");

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        when(foodService.getProductByBarcode("validBarcode")).thenReturn(foodFacts);
        Product product = new Product();
        when(productFactory.create(anyString(), anyString(), anyString(), anyDouble(), anyDouble(), anyString(), anyChar(), anyString())).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.create(request);

        assertEquals(product, result);
    }

    @Test
    void create_withNullBarcode_throwsBadRequest() {
        CreateProductDTO request = mock(CreateProductDTO.class);
        when(request.getBarcodeId()).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> productService.create(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Barcode ID is required", exception.getReason());
    }

    @Test
    void create_withEmptyBarcode_throwsBadRequest() {
        CreateProductDTO request = mock(CreateProductDTO.class);
        when(request.getBarcodeId()).thenReturn("");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> productService.create(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Barcode ID is required", exception.getReason());
    }

    @Test
    void create_withZeroPrice_throwsBadRequest() {
        CreateProductDTO request = mock(CreateProductDTO.class);
        when(request.getBarcodeId()).thenReturn("validBarcode");
        when(request.getPrice()).thenReturn(0.0);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> productService.create(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Price is required", exception.getReason());
    }

    @Test
    void create_withNegativePrice_throwsBadRequest() {
        CreateProductDTO request = mock(CreateProductDTO.class);
        when(request.getBarcodeId()).thenReturn("validBarcode");
        when(request.getPrice()).thenReturn(-1.0);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> productService.create(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Price is required", exception.getReason());
    }

    @Test
    void create_withNonExistentBarcode_throwsNotFound() {
        CreateProductDTO request = mock(CreateProductDTO.class);
        when(request.getBarcodeId()).thenReturn("nonExistentBarcode");
        when(request.getPrice()).thenReturn(10.0);
        when(foodService.getProductByBarcode("nonExistentBarcode")).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> productService.create(request));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Product not found for the given barcode", exception.getReason());
    }
}
