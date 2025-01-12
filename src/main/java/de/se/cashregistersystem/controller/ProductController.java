package de.se.cashregistersystem.controller;

import de.se.cashregistersystem.dto.CreateProductDTO;
import de.se.cashregistersystem.entity.Item;
import de.se.cashregistersystem.factory.ItemFactory;
import de.se.cashregistersystem.repository.ItemRepository;
import de.se.cashregistersystem.service.OpenFoodFactsService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private OpenFoodFactsService foodService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    ItemFactory itemFactory;
    @PostMapping("/create")
    public ResponseEntity<Item> create(@RequestBody CreateProductDTO request) {
        try {
            if (request.getBarcodeId() == null || request.getBarcodeId().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Barcode ID is required");
            }

            JSONObject foodFacts = foodService.getProductByBarcode(request.getBarcodeId());

            if (foodFacts == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found for the given barcode");
            }

            String brandName = foodFacts.optString("brands", "");
            String productName = foodFacts.optString("product_name", "");
            String categories = foodFacts.optString("categories", "");
            boolean fluid = categories.toLowerCase().contains("getränke");


            Item item = itemRepository.save(itemFactory.create(
                    cleanString(productName),
                    cleanString(request.getBarcodeId()),
                    cleanString(brandName),
                    fluid,
                    request.getPrice(),
                    cleanString(categories)
            ));

            System.out.println(item.toString());

            return new ResponseEntity<Item>(item, HttpStatus.CREATED);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred", e);
        }
    }

    private String cleanString(String input) {
        return input == null ? null : input.replaceAll("\u0000", "");
    }
}
