package de.se.cashregistersystem.service;

import de.se.cashregistersystem.dto.CreateProductDTO;
import de.se.cashregistersystem.entity.Product;
import de.se.cashregistersystem.factory.ProductFactory;
import de.se.cashregistersystem.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductService {
    @Autowired
    private OpenFoodFactsService foodService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductFactory productFactory;
    public Product create(CreateProductDTO request) {
        if (request.getBarcodeId() == null || request.getBarcodeId().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Barcode ID is required");
        }
        if (request.getPrice() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price is required");
        }

        JSONObject foodFacts = foodService.getProductByBarcode(request.getBarcodeId());

        if (foodFacts == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found for the given barcode");
        }

        String brandName = foodFacts.optString("brands", "");
        String productName = foodFacts.optString("product_name", "");
        String categories = foodFacts.optString("categories", "");
        char nutriscore = 'A';
        String imgUrl = "";

        Product product = productFactory.create(
                cleanString(productName),
                cleanString(request.getBarcodeId()),
                cleanString(brandName),
                request.getPledgeValue(),
                request.getPrice(),
                cleanString(categories),
                nutriscore,
                cleanString(imgUrl)
        );
        product = productRepository.save(product);
        return product;
    }
    private String cleanString(String input) {
        return input == null ? null : input.replaceAll("\u0000", "");
    }
}



