package de.se.cashregistersystem.controller;

import de.se.cashregistersystem.dto.CreateProductDTO;
import de.se.cashregistersystem.entity.Product;
import de.se.cashregistersystem.factory.ProductFactory;
import de.se.cashregistersystem.repository.ProductRepository;
import de.se.cashregistersystem.service.OpenFoodFactsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @Mock
    private OpenFoodFactsService foodService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductFactory productFactory;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_withValidRequest_createsProduct() {
        CreateProductDTO request = new CreateProductDTO("validBarcode", 10.0, 1.0);
        JSONObject foodFacts = new JSONObject();
        try {
            foodFacts.put("brands", "BrandName");
            foodFacts.put("product_name", "ProductName");
            foodFacts.put("categories", "Category");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        when(foodService.getProductByBarcode(request.getBarcodeId())).thenReturn(foodFacts);
        Product product = new Product();
        when(productFactory.create(anyString(), anyString(), anyString(), anyDouble(), anyDouble(), anyString(), anyChar(), anyString())).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ResponseEntity<Product> response = productController.create(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(product, response.getBody());
    }

    @Test
    void create_withMissingBarcode_throwsBadRequest() {
        CreateProductDTO request = new CreateProductDTO("", 10.0, 1.0);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> productController.create(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Barcode ID is required", exception.getReason());
    }

    @Test
    void create_withInvalidPrice_throwsBadRequest() {
        CreateProductDTO request = new CreateProductDTO("validBarcode", 0, 1.0);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> productController.create(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Price is required", exception.getReason());
    }

    @Test
    void create_withNonExistentProduct_throwsNotFound() {
        CreateProductDTO request = new CreateProductDTO("validBarcode", 10.0, 1.0);
        when(foodService.getProductByBarcode(request.getBarcodeId())).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> productController.create(request));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Product not found for the given barcode", exception.getReason());
    }
}