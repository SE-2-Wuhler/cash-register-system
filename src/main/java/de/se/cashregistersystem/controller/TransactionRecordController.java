package de.se.cashregistersystem.controller;

import de.se.cashregistersystem.dto.ItemWithQuantityDTO;
import de.se.cashregistersystem.dto.TransactionRequestDTO;
import de.se.cashregistersystem.entity.Item;
import de.se.cashregistersystem.entity.TransactionRecord;
import de.se.cashregistersystem.repository.ItemRepository;
import de.se.cashregistersystem.repository.ItemTransactionRepository;
import de.se.cashregistersystem.repository.PledgeTransactionRepository;
import de.se.cashregistersystem.repository.TransactionRecordRepository;
import de.se.cashregistersystem.service.PayPalService;
import de.se.cashregistersystem.service.PrintingService;
import de.se.cashregistersystem.service.TransactionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/transaction")
public class TransactionRecordController {

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Autowired
    private ItemTransactionRepository itemTransactionRepository;

    @Autowired
    private PledgeTransactionRepository pledgeTransactionRepository;

    @Autowired
    private TransactionRecordService service;
    @Autowired
    private PayPalService payPalService;
    @Autowired
    private PrintingService printingService;
    @Autowired
    private ItemRepository itemRepository;

    @GetMapping("/{id}")
    public ResponseEntity<TransactionRecord> getTransactionById(@PathVariable UUID id){
        Optional<TransactionRecord> transaction = transactionRecordRepository.findById(id);
        if (!transaction.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found.");
        }
        return new ResponseEntity<>(transaction.get(), HttpStatus.OK);
    }
    @PostMapping("/create")
    public ResponseEntity<Object> create(@RequestBody TransactionRequestDTO requestDTO){
        try{
            UUID transactionRecord = service.createTransactionRecord(requestDTO.getItems(), requestDTO.getPledges());
            if(transactionRecord == null){
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create new Transaction");
            }
            return new ResponseEntity<>(transactionRecord, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to Create new Transaction " + e.getMessage());
        }
    }
    @PostMapping("/complete")
    public ResponseEntity<String> completeTransaction(@RequestBody String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Order ID cannot be null or empty"
            );
        }

        try {
            // Verify PayPal payment
            UUID transactionId = payPalService.verifyPayment(orderId);

            // Get items for transaction
            List<UUID> ids = itemTransactionRepository.getItemsByTransactionId(transactionId);
            if (ids == null || ids.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No items found for transaction: " + transactionId
                );
            }

            // Get item details
            List<Item> items = itemRepository.findAllById(ids);
            if (items.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Items not found in database"
                );
            }

            // Print receipt
            printingService.printReceipt(items);

            return new ResponseEntity<String>("Transaction completed", HttpStatus.OK);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to complete transaction: " + e.getMessage()
            );
        }
    }
}
