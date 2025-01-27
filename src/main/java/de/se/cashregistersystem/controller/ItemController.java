package de.se.cashregistersystem.controller;

import de.se.cashregistersystem.entity.Product;
import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.repository.ProductRepository;
import de.se.cashregistersystem.repository.PledgeRepository;
import de.se.cashregistersystem.dto.Scanable;
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
    @GetMapping("/notscanables")
    public ResponseEntity<List<Product>> getById() {
        Optional<List<Product>> items = productRepository.findAllByIsNonScanableTrue();
        if(items.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Did not find nonscanable items");
        }
        return new ResponseEntity<>(items.get(), HttpStatus.OK);
    }
    }