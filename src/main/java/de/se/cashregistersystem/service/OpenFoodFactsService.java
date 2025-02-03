package de.se.cashregistersystem.service;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OpenFoodFactsService {

    private final RestTemplate restTemplate;
    private final String OPEN_FOOD_FACTS_API = "https://world.openfoodfacts.org/api/v0";


    public OpenFoodFactsService() {
        this.restTemplate = new RestTemplate();
    }
    
    public OpenFoodFactsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public JSONObject getProductByBarcode(String barcode) {
        // Validate barcode
        if (barcode == null || barcode.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Barcode cannot be null or empty"
            );
        }

        try {
            String jsonResponse = restTemplate.getForObject(
                    OPEN_FOOD_FACTS_API + "/product/{barcode}.json",
                    String.class,
                    barcode
            );

            if (jsonResponse == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Empty response from OpenFoodFacts API"
                );
            }

            JSONObject fullResponse = new JSONObject(jsonResponse);
            JSONObject product = fullResponse.optJSONObject("product");
            if (product == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Product data missing in API response"
                );
            }

            return product;

        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(
                    e.getStatusCode(),
                    "Error fetching product data: " + e.getStatusText()
            );
        } catch (HttpServerErrorException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "OpenFoodFacts API error: " + e.getStatusText()
            );
        } catch (JSONException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error parsing API response: " + e.getMessage()
            );
        } catch (RestClientException e) {
            throw new ResponseStatusException(
                    HttpStatus.GATEWAY_TIMEOUT,
                    "Unable to connect to OpenFoodFacts API: " + e.getMessage()
            );
        }
    }
}