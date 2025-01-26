package de.se.cashregistersystem.controller;

import de.se.cashregistersystem.entity.Product;
import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.repository.ProductRepository;
import de.se.cashregistersystem.repository.PledgeRepository;
import de.se.cashregistersystem.util.Scanable;
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
public class ItemController {
    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PledgeRepository pledgeRepository;
    @GetMapping("/{barcode_id}")
    public ResponseEntity<Scanable> getById(@PathVariable("barcode_id") String barcodeId) {
        logger.debug("Searching for item with barcode: {}", barcodeId);

        // Validate barcode format
        if (barcodeId == null || barcodeId.trim().isEmpty()) {
            logger.error("Invalid barcode provided: {}", barcodeId);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Barcode cannot be null or empty"
            );
        }

        try {
            // Try to find item
            Optional<Product> item = productRepository.findItemByBarcodeId(barcodeId);
            if (item.isPresent()) {
                logger.debug("Found item for barcode: {}", barcodeId);
                return new ResponseEntity<>(item.get(), HttpStatus.OK);
            }

            // Try to find pledge
            Optional<Pledge> pledge = pledgeRepository.findPledgeByBarcodeId(barcodeId);
            if (pledge.isPresent()) {
                logger.debug("Found pledge for barcode: {}", barcodeId);
                return new ResponseEntity<>(pledge.get(), HttpStatus.OK);
            }

            // Neither item nor pledge found
            logger.warn("No item or pledge found for barcode: {}", barcodeId);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No item or pledge found with barcode: " + barcodeId
            );

        } catch (Exception e) {
            // If it's already a ResponseStatusException, rethrow it
            if (e instanceof ResponseStatusException) {
                throw e;
            }
            // Handle any other unexpected errors
            logger.error("Error while processing barcode {}: {}", barcodeId, e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error processing request for barcode: " + barcodeId,
                    e
            );
        }
    }
    @GetMapping("/notscanables")
    public ResponseEntity<List<Product>> getById() {
        Optional<List<Product>> items = productRepository.findAllByIsNonScanableTrue();
        if(!items.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Did not find nonscanable items");
        }
        return new ResponseEntity<>(items.get(), HttpStatus.OK);
    }
    }