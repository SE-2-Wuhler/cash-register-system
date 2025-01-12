package de.se.cashregistersystem.controller;

import de.se.cashregistersystem.dto.ItemWithQuantityDTO;
import de.se.cashregistersystem.dto.PledgeDTO;
import de.se.cashregistersystem.entity.Item;
import de.se.cashregistersystem.entity.Pledge;
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
    public ResponseEntity<UUID> createPledge(@RequestBody ItemWithQuantityDTO[] itemWithQuantityDTO) {
        logger.debug("Attempting to create pledge with {} items", itemWithQuantityDTO.length);

        if (itemWithQuantityDTO.length == 0) {
            logger.error("Received empty or null item array for pledge creation");
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot create pledge with empty item list"
            );
        }

        try {
            UUID id = service.createPledge(itemWithQuantityDTO);
            logger.info("Successfully created pledge with ID: {}", id);
            return new ResponseEntity<>(
                    id,
                    HttpStatus.CREATED
            );

        } catch (IllegalArgumentException e) {
            logger.error("Invalid data provided for pledge creation: {}", e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid pledge data: " + e.getMessage()
            );

        } catch (Exception e) {
            logger.error("Error creating pledge: {}", e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error creating pledge: " + e.getMessage(),
                    e
            );
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Item>> getAll() {
        logger.debug("Fetching all pledge items");

        try {
            List<Item> items = service.getAllPledgeItems();

            if (items.isEmpty()) {
                logger.info("No pledge items found");
                throw new ResponseStatusException(
                        HttpStatus.NO_CONTENT,
                        "No pledge items available"
                );
            }

            logger.debug("Successfully retrieved {} pledge items", items.size());
            return new ResponseEntity<>(items, HttpStatus.OK);

        } catch (ResponseStatusException e) {
            throw e;

        } catch (Exception e) {
            logger.error("Error fetching pledge items: {}", e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error fetching pledge items: " + e.getMessage(),
                    e
            );
        }
    }
}