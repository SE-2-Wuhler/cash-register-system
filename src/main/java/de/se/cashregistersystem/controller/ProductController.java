package de.se.cashregistersystem.controller;

import de.se.cashregistersystem.dto.CreateProductDTO;
import de.se.cashregistersystem.factory.ItemFactory;
import de.se.cashregistersystem.repository.ItemRepository;
import de.se.cashregistersystem.service.OpenFoodFactsService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private OpenFoodFactsService foodService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    ItemFactory itemFactory;
    @GetMapping("/create")
    public ResponseEntity<String> create(@RequestBody CreateProductDTO request) {

        try {
            JSONObject foodFacts = foodService.getProductByBarcode(request.getBarcodeId());

            // Extrahieren der brand_id (in diesem Fall nehmen wir einfach den Markennamen)
            String brandName = foodFacts.getString("brands");
            String productName = foodFacts.getString("product_name");
            // Bestimmen der Art des Produkts
            String categories = foodFacts.getString("categories");
            boolean fluid = false;
            String containerType = "Unbekannt";

            if (categories.toLowerCase().contains("getränke")) {
                fluid = true;
                return new ResponseEntity<String>(HttpStatus.OK);


            }
            itemRepository.save(itemFactory.create(cleanString(productName),cleanString(request.getBarcodeId()), cleanString(brandName),fluid, request.getPrice(), cleanString(categories)));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>("Product has been created",HttpStatus.CREATED);
    }
    private String cleanString(String input) {
        if (input == null) return null;
        return input.replaceAll("\u0000", "");
    }
}
