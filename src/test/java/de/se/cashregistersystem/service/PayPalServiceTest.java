package de.se.cashregistersystem.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void verifyPayment_withApprovedStatus_returnsTransactionId() {
        String orderId = "testOrderId";
        String accessToken = "testAccessToken";
        String transactionId = UUID.randomUUID().toString();

        when(restTemplate.exchange(
                eq(PAYPAL_API + "/v2/checkout/orders/" + orderId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)))
                .thenReturn(new ResponseEntity<>(Map.of(
                        "status", "APPROVED",
                        "purchase_units", List.of(Map.of("reference_id", transactionId))
                ), HttpStatus.OK));

        UUID result = payPalService.verifyPayment(orderId, accessToken);

        assertEquals(UUID.fromString(transactionId), result);
    }

    @Test
    void verifyPayment_withMissingStatus_throwsBadGateway() {
        String orderId = "testOrderId";
        String accessToken = "testAccessToken";

        when(restTemplate.exchange(
                eq(PAYPAL_API + "/v2/checkout/orders/" + orderId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)))
                .thenReturn(new ResponseEntity<>(Map.of(), HttpStatus.OK));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> payPalService.verifyPayment(orderId, accessToken));

        assertEquals(HttpStatus.BAD_GATEWAY, exception.getStatusCode());
        assertEquals("Status field missing in PayPal response", exception.getReason());
    }

    @Test
    void verifyPayment_withIncompletePayment_throwsPaymentRequired() {
        String orderId = "testOrderId";
        String accessToken = "testAccessToken";

        when(restTemplate.exchange(
                eq(PAYPAL_API + "/v2/checkout/orders/" + orderId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)))
                .thenReturn(new ResponseEntity<>(Map.of("status", "PENDING"), HttpStatus.OK));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> payPalService.verifyPayment(orderId, accessToken));

        assertEquals(HttpStatus.PAYMENT_REQUIRED, exception.getStatusCode());
        assertEquals("Payment incomplete. Order status is PENDING", exception.getReason());
    }

    @Test
    void verifyPayment_withMissingPurchaseUnits_throwsBadGateway() {
        String orderId = "testOrderId";
        String accessToken = "testAccessToken";

        when(restTemplate.exchange(
                eq(PAYPAL_API + "/v2/checkout/orders/" + orderId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)))
                .thenReturn(new ResponseEntity<>(Map.of("status", "APPROVED"), HttpStatus.OK));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> payPalService.verifyPayment(orderId, accessToken));

        assertEquals(HttpStatus.BAD_GATEWAY, exception.getStatusCode());
        assertEquals("Purchase units missing in PayPal response", exception.getReason());
    }

    @Test
    void verifyPayment_withInvalidTransactionId_throwsBadGateway() {
        String orderId = "testOrderId";
        String accessToken = "testAccessToken";

        when(restTemplate.exchange(
                eq(PAYPAL_API + "/v2/checkout/orders/" + orderId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)))
                .thenReturn(new ResponseEntity<>(Map.of(
                        "status", "APPROVED",
                        "purchase_units", List.of(Map.of("reference_id", "default"))
                ), HttpStatus.OK));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> payPalService.verifyPayment(orderId, accessToken));

        assertEquals(HttpStatus.BAD_GATEWAY, exception.getStatusCode());
        assertEquals("TransactionID is not set in PayPal response", exception.getReason());
    }

    @Test
    void verifyPayment_withInvalidTransactionIdFormat_throwsBadGateway() {
        String orderId = "testOrderId";
        String accessToken = "testAccessToken";

        when(restTemplate.exchange(
                eq(PAYPAL_API + "/v2/checkout/orders/" + orderId),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)))
                .thenReturn(new ResponseEntity<>(Map.of(
                        "status", "APPROVED",
                        "purchase_units", List.of(Map.of("reference_id", "invalid-uuid"))
                ), HttpStatus.OK));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> payPalService.verifyPayment(orderId, accessToken));

        assertEquals(HttpStatus.BAD_GATEWAY, exception.getStatusCode());
        assertEquals("Invalid transaction ID format received from PayPal: invalid-uuid", exception.getReason());
    }
    @Test
    void authenticate_withValidCredentials_returnsAccessToken() {
        String accessToken = "testAccessToken";
        Map<String, Object> responseBody = Map.of("access_token", accessToken);

        when(restTemplate.exchange(
                eq(PAYPAL_API + "/v1/oauth2/token"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(new ResponseEntity<>(responseBody, HttpStatus.OK));

        String result = payPalService.authenticate();

        assertEquals(accessToken, result);
    }

    @Test
    void authenticate_withInvalidResponse_throwsInternalServerError() {
        when(restTemplate.exchange(
                eq(PAYPAL_API + "/v1/oauth2/token"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> payPalService.authenticate());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("Invalid response from PayPal authentication", exception.getReason());
    }

    @Test
    void authenticate_withNullResponseBody_throwsInternalServerError() {
        when(restTemplate.exchange(
                eq(PAYPAL_API + "/v1/oauth2/token"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> payPalService.authenticate());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("Invalid response from PayPal authentication", exception.getReason());
    }

    @Test
    void authenticate_withMissingAccessToken_throwsInternalServerError() {
        Map<String, Object> responseBody = Map.of();

        when(restTemplate.exchange(
                eq(PAYPAL_API + "/v1/oauth2/token"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(new ResponseEntity<>(responseBody, HttpStatus.OK));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> payPalService.authenticate());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("Invalid response from PayPal authentication", exception.getReason());
    }
}