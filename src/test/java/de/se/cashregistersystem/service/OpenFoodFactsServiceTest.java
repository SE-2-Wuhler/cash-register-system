package de.se.cashregistersystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;


class OpenFoodFactsServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OpenFoodFactsService openFoodFactsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetProductByBarcode_NullBarcode() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            openFoodFactsService.getProductByBarcode(null);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Barcode cannot be null or empty", exception.getReason());
    }

    @Test
    void testGetProductByBarcode_EmptyBarcode() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            openFoodFactsService.getProductByBarcode(" ");
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Barcode cannot be null or empty", exception.getReason());
    }

    @Test
    void testGetProductByBarcode_ValidBarcode() throws Exception {
        String jsonResponse = "{\"product\": {\"name\": \"Test Product\"}}";
        when(restTemplate.getForObject(anyString(), eq(String.class), anyString())).thenReturn(jsonResponse);

        JSONObject product = openFoodFactsService.getProductByBarcode("123456789");

        assertNotNull(product);
        assertEquals("Test Product", product.getString("name"));
    }

    @Test
    void testGetProductByBarcode_EmptyResponse() {
        when(restTemplate.getForObject(anyString(), eq(String.class), anyString())).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            openFoodFactsService.getProductByBarcode("123456789");
        });
        assertEquals(HttpStatus.BAD_GATEWAY, exception.getStatusCode());
        assertEquals("Empty response from OpenFoodFacts API", exception.getReason());
    }

    @Test
    void testGetProductByBarcode_MissingProductData() {
        String jsonResponse = "{}";
        when(restTemplate.getForObject(anyString(), eq(String.class), anyString())).thenReturn(jsonResponse);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            openFoodFactsService.getProductByBarcode("123456789");
        });
        assertEquals(HttpStatus.BAD_GATEWAY, exception.getStatusCode());
        assertEquals("Product data missing in API response", exception.getReason());
    }

    @Test
    void testGetProductByBarcode_HttpClientErrorException() {
        when(restTemplate.getForObject(anyString(), eq(String.class), anyString())).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not Found"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            openFoodFactsService.getProductByBarcode("123456789");
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Error fetching product data: Not Found", exception.getReason());
    }

    @Test
    void testGetProductByBarcode_HttpServerErrorException() {
        when(restTemplate.getForObject(anyString(), eq(String.class), anyString())).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            openFoodFactsService.getProductByBarcode("123456789");
        });
        assertEquals(HttpStatus.BAD_GATEWAY, exception.getStatusCode());
        assertEquals("OpenFoodFacts API error: Internal Server Error", exception.getReason());
    }

    @Test
    void testGetProductByBarcode_JSONException() {
        String invalidJsonResponse = "{invalid json}";
        when(restTemplate.getForObject(anyString(), eq(String.class), anyString())).thenReturn(invalidJsonResponse);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            openFoodFactsService.getProductByBarcode("123456789");
        });
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error parsing API response"));
    }

    @Test
    void testGetProductByBarcode_RestClientException() {
        when(restTemplate.getForObject(anyString(), eq(String.class), anyString())).thenThrow(new RestClientException("Connection timeout"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            openFoodFactsService.getProductByBarcode("123456789");
        });
        assertEquals(HttpStatus.GATEWAY_TIMEOUT, exception.getStatusCode());
        assertEquals("Unable to connect to OpenFoodFacts API: Connection timeout", exception.getReason());
    }
}