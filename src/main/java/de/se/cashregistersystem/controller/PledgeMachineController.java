package de.se.cashregistersystem.controller;

import de.se.cashregistersystem.dto.ProductWithQuantityDTO;
import de.se.cashregistersystem.entity.Product;
import de.se.cashregistersystem.repository.PledgeRepository;
import de.se.cashregistersystem.service.PledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/pledge")
public class PledgeMachineController {
    private static final Logger logger = LoggerFactory.getLogger(PledgeMachineController.class);

    @Autowired
    private PledgeRepository pledgeRepository;
    @Autowired
    private PledgeService service;

    @PostMapping("/create")
    public ResponseEntity<UUID> createPledge(@RequestBody ProductWithQuantityDTO[] productWithQuantityDTO) {
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