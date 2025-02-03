package de.se.cashregistersystem.controller;

import de.se.cashregistersystem.entity.Product;
import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.dto.Scanable;
import de.se.cashregistersystem.service.ItemService;
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

import java.util.List;


@RestController
@RequestMapping("/item")
@Tag(name = "Item", description = "The Item API")
public class ItemController {

    @Autowired
    private ItemService service;


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


            Scanable item = service.getById(barcodeId);
            return new ResponseEntity<>(item, HttpStatus.OK);
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
        List<Product> items = service.getAllNonScanableProducts();
        return new ResponseEntity<>(items, HttpStatus.OK);
    }
}