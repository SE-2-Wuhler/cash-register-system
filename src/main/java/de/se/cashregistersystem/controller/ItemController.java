package de.se.cashregistersystem.controller;

import de.se.cashregistersystem.entity.Product;
import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.repository.ProductRepository;
import de.se.cashregistersystem.repository.PledgeRepository;
import de.se.cashregistersystem.dto.Scanable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/item")
@Tag(name = "Item", description = "The Item API")
public class ItemController {
    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PledgeRepository pledgeRepository;

    @Operation(summary = "Get an item by its barcode",
            description = "Returns a product or pledge based on the provided barcode ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Found the item",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(oneOf = {Product.class, Pledge.class}))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid barcode supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Item not found",
                    content = @Content)
    })
    @GetMapping("/{barcode_id}")
    public ResponseEntity<Scanable> getById(
            @Parameter(description = "Barcode ID of the item to be searched")
            @PathVariable("barcode_id") String barcodeId) {
        logger.debug("Searching for item with barcode: {}", barcodeId);

        // Validate barcode format
        if (barcodeId == null || barcodeId.trim().isEmpty()) {
            logger.error("Invalid barcode provided: {}", barcodeId);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Barcode cannot be null or empty"
            );
        }

        Optional<Product> item = productRepository.findItemByBarcodeId(barcodeId);
        if (item.isPresent()) {
            logger.debug("Found product for barcode: {}", barcodeId);
            return new ResponseEntity<>(item.get(), HttpStatus.OK);
        }

        // Try to find pledge
        Optional<Pledge> pledge = pledgeRepository.findPledgeByBarcodeId(barcodeId);
        if (pledge.isPresent() && !pledge.get().isValidated()) {
            logger.debug("Found pledge for barcode: {}", barcodeId);
            return new ResponseEntity<>(pledge.get(), HttpStatus.OK);
        }

        // Neither item nor pledge found
        logger.warn("No product or valid pledge found for barcode: {}", barcodeId);
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "No product or valid pledge found with barcode: " + barcodeId
        );
    }

    @Operation(summary = "Get all non-scannable items",
            description = "Returns a list of all products that are marked as non-scannable")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Found non-scannable items",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Product.class))}),
            @ApiResponse(responseCode = "404",
                    description = "No non-scannable items found",
                    content = @Content)
    })
    @GetMapping("/notscanables")
    public ResponseEntity<List<Product>> getById() {
        Optional<List<Product>> items = productRepository.findAllByIsNonScanableTrue();
        if(items.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Did not find nonscanable items");
        }
        return new ResponseEntity<>(items.get(), HttpStatus.OK);
    }
}