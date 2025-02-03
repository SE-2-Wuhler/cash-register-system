package de.se.cashregistersystem.service;


import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PayPalService {

    @Autowired
    private static final String PAYPAL_API = "https://api-m.sandbox.paypal.com";

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;

    public PayPalService() {
        this.restTemplate = new RestTemplate();
    }

    public PayPalService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UUID verifyPayment(String orderId) {
        String accessToken;
        try {
            accessToken = this.getAccessToken();
        } catch (RestClientException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Failed to authenticate with PayPal: " + e.getMessage()
            );
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        Map<String, Object> body;
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    PAYPAL_API + "/v2/checkout/orders/" + orderId,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );
            body = response.getBody();
            if (body == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Empty response from PayPal API"
                );
            }
        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Client error when calling PayPal API: " + e.getStatusText()
            );
        } catch (HttpServerErrorException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "PayPal server error: " + e.getStatusText()
            );
        } catch (RestClientException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error communicating with PayPal API: " + e.getMessage()
            );
        }

        String status = (String) body.get("status");
        if (status == null || status.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Status field missing in PayPal response"
            );
        }
        if (!"APPROVED".equals(status)) {
            throw new ResponseStatusException(
                    HttpStatus.PAYMENT_REQUIRED,
                    "Payment incomplete. Order status is " + status
            );
        }
        @SuppressWarnings("unchecked")
        List<HashMap<String, String>> purchaseUnits = (List<HashMap<String, String>>) body.get("purchase_units");
        if (purchaseUnits == null || purchaseUnits.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Purchase units missing in PayPal response"
            );
        }

        HashMap<String, String> purchaseUnit = purchaseUnits.get(0);
        String transactionId = purchaseUnit.get("reference_id");
        if ("default".equals(transactionId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "TransactionID is not set in PayPal response"
            );
        }

        try {

            return UUID.fromString(transactionId);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Invalid transaction ID format received from PayPal: " + transactionId
            );
        }
    }

    private String getAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        String auth = clientId + ":" + clientSecret;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        headers.set("Authorization", "Basic " + new String(encodedAuth));

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                PAYPAL_API + "/v1/oauth2/token",
                HttpMethod.POST,
                entity,
                Map.class
        );

        Map<String, Object> body = response.getBody();
        if (body == null || !body.containsKey("access_token")) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Invalid response from PayPal authentication"
            );
        }

        return (String) body.get("access_token");
    }
}