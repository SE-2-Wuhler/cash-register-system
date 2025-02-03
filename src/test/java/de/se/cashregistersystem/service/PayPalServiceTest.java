package de.se.cashregistersystem.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;


public class PayPalServiceTest {

    private static final String PAYPAL_API = "https://api-m.sandbox.paypal.com";

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PayPalService payPalService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testVerifyPayment_Success() {
        String orderId = "testOrderId";
        String accessToken = "testAccessToken";
        String transactionId = UUID.randomUUID().toString();

        when(restTemplate.exchange(
                eq(PAYPAL_API + "/v1/oauth2/token"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)))
                .thenReturn(new ResponseEntity<>(Collections.singletonMap("access_token", accessToken), HttpStatus.OK));

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", "COMPLETED");
        List<Map<String, Object>> purchaseUnits = new ArrayList<>();
        Map<String, Object> purchaseUnit = new HashMap<>();
        purchaseUnit.put("reference_id", transactionId);
        purchaseUnits.add(purchaseUnit);

        responseBody.put("purchase_units", purchaseUnits);

        when(restTemplate.exchange(
                eq(PAYPAL_API + "/v2/checkout/orders/" + orderId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class))).thenReturn(new ResponseEntity<>(responseBody, HttpStatus.OK));

        UUID result = payPalService.verifyPayment(orderId);
        assert result.equals(UUID.fromString(transactionId));
    }

    @Test
    public void testVerifyPayment_Failure_InvalidStatus() {
        String orderId = "testOrderId";
        String accessToken = "testAccessToken";

        when(restTemplate.exchange(
                eq(PAYPAL_API + "/v1/oauth2/token"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)))
                .thenReturn(new ResponseEntity<>(Collections.singletonMap("access_token", accessToken), HttpStatus.OK));

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", "PENDING");

        when(restTemplate.exchange(
                eq(PAYPAL_API + "/v2/checkout/orders/" + orderId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class))).thenReturn(new ResponseEntity<>(responseBody, HttpStatus.OK));

        try {
            payPalService.verifyPayment(orderId);
        } catch (ResponseStatusException e) {
            assert e.getStatusCode() == HttpStatus.PAYMENT_REQUIRED;
        }
    }

    @Test
    public void testVerifyPayment_Failure_EmptyResponseBody() {
        String orderId = "testOrderId";
        String accessToken = "testAccessToken";

        when(restTemplate.exchange(
                eq(PAYPAL_API + "/v1/oauth2/token"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)))
                .thenReturn(new ResponseEntity<>(Collections.singletonMap("access_token", accessToken), HttpStatus.OK));

        when(restTemplate.exchange(
                eq(PAYPAL_API + "/v2/checkout/orders/" + orderId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class))).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        try {
            payPalService.verifyPayment(orderId);
        } catch (ResponseStatusException e) {
            assert e.getStatusCode() == HttpStatus.BAD_GATEWAY;
        }
    }

    @Test
    public void testVerifyPayment_Failure_NetworkError() {
        String orderId = "testOrderId";

        when(restTemplate.exchange(
                eq(PAYPAL_API + "/v1/oauth2/token"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)))
                .thenThrow(new RestClientException("Network error"));

        try {
            payPalService.verifyPayment(orderId);
        } catch (ResponseStatusException e) {
            assert e.getStatusCode() == HttpStatus.UNAUTHORIZED;
        }
    }

    @Test
    public void testVerifyPayment_Failure_ServerError() {
        String orderId = "testOrderId";
        String accessToken = "testAccessToken";

        when(restTemplate.exchange(
                eq(PAYPAL_API + "/v1/oauth2/token"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)))
                .thenReturn(new ResponseEntity<>(Collections.singletonMap("access_token", accessToken), HttpStatus.OK));

        when(restTemplate.exchange(
                eq(PAYPAL_API + "/v2/checkout/orders/" + orderId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error"));

        try {
            payPalService.verifyPayment(orderId);
        } catch (ResponseStatusException e) {
            assert e.getStatusCode() == HttpStatus.BAD_GATEWAY;
        }
    }

    @Test
    public void testVerifyPayment_Failure_InvalidTransactionId() {
        String orderId = "testOrderId";
        String accessToken = "testAccessToken";

        when(restTemplate.exchange(
                eq(PAYPAL_API + "/v1/oauth2/token"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)))
                .thenReturn(new ResponseEntity<>(Collections.singletonMap("access_token", accessToken), HttpStatus.OK));

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", "COMPLETED");
        List<Map<String, Object>> purchaseUnits = new ArrayList<>();
        Map<String, Object> purchaseUnit = new HashMap<>();
        purchaseUnit.put("reference_id", "default");
        purchaseUnits.add(purchaseUnit);

        responseBody.put("purchase_units", purchaseUnits);

        when(restTemplate.exchange(
                eq(PAYPAL_API + "/v2/checkout/orders/" + orderId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class))).thenReturn(new ResponseEntity<>(responseBody, HttpStatus.OK));

        try {
            payPalService.verifyPayment(orderId);
        } catch (ResponseStatusException e) {
            assert e.getStatusCode() == HttpStatus.BAD_GATEWAY;
        }
    }
}