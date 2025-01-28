package de.se.cashregistersystem.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.se.cashregistersystem.dto.ProductWithQuantityDTO;
import de.se.cashregistersystem.entity.Product;
import de.se.cashregistersystem.service.PledgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/pledge")
@Tag(name = "Pledge Machine", description = "API endpoints for managing pledges and pledge items")
public class PledgeMachineController {
    private static final Logger logger = LoggerFactory.getLogger(PledgeMachineController.class);

    @Autowired
    private PledgeService service;

    @Operation(summary = "Create a new pledge",
            description = "Creates a new pledge with the provided products and their quantities")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Pledge created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UUID.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input - empty product list",
                    content = @Content
            )
    })
    @PostMapping("/create")
    public ResponseEntity<UUID> createPledge(
            @Parameter(
                    description = "Array of products with their quantities to create a pledge",
                    required = true
            )
            @RequestBody ProductWithQuantityDTO[] productWithQuantityDTO) {
        logger.debug("Attempting to create pledge with {} items", productWithQuantityDTO.length);

        if (productWithQuantityDTO.length == 0) {
            logger.error("Received empty or null item array for pledge creation");
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot create pledge with empty item list"
            );
        }
        UUID pledgeId = service.createPledge(productWithQuantityDTO);
        logger.info("Successfully created pledge with ID: {}", pledgeId);
        return new ResponseEntity<>(
                pledgeId,
                HttpStatus.CREATED
        );
    }

    @Operation(summary = "Get all products with pledges",
            description = "Retrieves a list of all products that are associated with pledges")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved pledge items",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Product.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No pledge items found",
                    content = @Content
            )
    })
    @GetMapping("/get-all-products-with-pledge")
    public ResponseEntity<List<Product>> getAll() {
        logger.debug("Fetching all pledge items");

        List<Product> products = service.getAllPledgeItems();

        if (products.isEmpty()) {
            logger.info("No pledge items found");
            throw new ResponseStatusException(
                    HttpStatus.NO_CONTENT,
                    "No pledge items available"
            );
        }

        logger.debug("Successfully retrieved {} pledge items", products.size());

        return new ResponseEntity<>(products, HttpStatus.OK);
    }
}