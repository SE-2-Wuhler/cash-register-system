package de.se.cashregistersystem.service;

import de.se.cashregistersystem.controller.ItemController;
import de.se.cashregistersystem.dto.Scanable;
import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.entity.Product;
import de.se.cashregistersystem.repository.PledgeRepository;
import de.se.cashregistersystem.repository.ProductRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;


@Service
public class ItemService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PledgeRepository pledgeRepository;

    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);


    public Scanable getById(String barcodeId) {
        logger.debug("Searching for item with barcode: {}", barcodeId);
        if (barcodeId == null || barcodeId.trim().isEmpty()) {
            logger.error("Invalid barcode provided: {}", barcodeId);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Barcode cannot be null or empty"
            );
        }

        Optional<Product> item = productRepository.findProductByBarcodeId(barcodeId);
        if (item.isPresent()) {
            logger.debug("Found product for barcode: {}", barcodeId);
            return item.get();
        }

        // Try to find pledge
        Optional<Pledge> pledge = pledgeRepository.findPledgeByBarcodeId(barcodeId);
        if (pledge.isPresent() && !pledge.get().isValidated()) {
            logger.debug("Found pledge for barcode: {}", barcodeId);
            return pledge.get();
        }

        // Neither item nor pledge found
        logger.warn("No product or valid pledge found for barcode: {}", barcodeId);
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "No product or valid pledge found with barcode: " + barcodeId
        );
    }

    public List<Product> getAllNonScanableProducts(){
        Optional<List<Product>> items = productRepository.findAllByIsNonScanableTrue();
        if(items.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Did not find nonscanable items");
        }
        return items.get();
    }


}
