package de.se.cashregistersystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.se.cashregistersystem.dto.CreateProductDTO;
import de.se.cashregistersystem.entity.Product;
import de.se.cashregistersystem.factory.ProductFactory;
import de.se.cashregistersystem.repository.ProductRepository;
import de.se.cashregistersystem.service.OpenFoodFactsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/product")
@Tag(name = "Product", description = "The Product API for managing products in the cash register system")
public class ProductController {
    @Autowired
    private OpenFoodFactsService foodService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    ProductFactory productFactory;

    @Operation(
            summary = "Create a new product",
            description = "Creates a new product using the provided details and OpenFoodFacts data"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Product created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Product.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input - Missing barcode ID or invalid price",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found in OpenFoodFacts database",
                    content = @Content
            )
    })
    @PostMapping("/create")
    public ResponseEntity<Product> create(
            @Parameter(
                    description = "Product creation request containing barcode, price, and pledge value",
                    required = true,
                    schema = @Schema(implementation = CreateProductDTO.class)
            )
            @RequestBody CreateProductDTO request) {

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

        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    private String cleanString(String input) {
        return input == null ? null : input.replaceAll("\u0000", "");
    }
}