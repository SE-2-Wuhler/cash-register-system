package de.se.cashregistersystem.service;

import de.se.cashregistersystem.controller.TransactionRecordController;
import de.se.cashregistersystem.entity.TransactionRecord;
import de.se.cashregistersystem.repository.TransactionRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class PayPalService {
    @Autowired
    private TransactionRecordRepository transactionRecordRepository;
    private static final String PAYPAL_API = "https://api-m.sandbox.paypal.com";

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;
    private RestTemplate restTemplate = new RestTemplate();
    public UUID verifyPayment(String orderId) throws RuntimeException{
        String accessToken = this.getAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                PAYPAL_API + "/v2/checkout/orders/" + orderId,
                HttpMethod.GET,
                entity,
                Map.class
        );
        Map<String, Object> body = response.getBody();
        String status = (String) body.get("status");
        if(!status.equals("COMPLETED")){
            throw new RuntimeException("Order is not paid.");
        }

        List<HashMap<String,String>> purchaseUnits = (List<HashMap<String,String>>) body.get("purchase_units");
        HashMap<String,String> purchaseUnit = purchaseUnits.get(0);
        String transactionId = purchaseUnit.get("reference_id");
        if ("default".equals(transactionId)){
            throw new RuntimeException("TransactionID is not set");
        }
        TransactionRecord record = transactionRecordRepository.findById(UUID.fromString(transactionId)).get();
        record.setStatus("paid");
        transactionRecordRepository.save(record);
        return UUID.fromString(transactionId);
    }
    private String getAccessToken(){

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
        System.out.println(body); // This is equivalent to console.log(data)

        return (String) body.get("access_token");
    }

}





