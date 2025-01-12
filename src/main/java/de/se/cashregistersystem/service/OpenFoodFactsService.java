package de.se.cashregistersystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OpenFoodFactsService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String OPEN_FOOD_FACTS_API = "https://world.openfoodfacts.org/api/v0";


    public JSONObject getProductByBarcode(String barcode) throws JSONException {
        String jsonResponse = restTemplate.getForObject(
                "https://world.openfoodfacts.org/api/v0/product/{barcode}.json",
                String.class,
                barcode
        );

        JSONObject fullResponse = new JSONObject(jsonResponse);
        return fullResponse.getJSONObject("product");
    }
}
