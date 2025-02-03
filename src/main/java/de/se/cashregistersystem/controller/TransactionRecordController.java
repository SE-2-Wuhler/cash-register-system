package de.se.cashregistersystem.controller;

import de.se.cashregistersystem.dto.CompleteTransactionDTO;
import de.se.cashregistersystem.dto.TransactionRequestDTO;
import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.entity.Product;
import de.se.cashregistersystem.entity.TransactionRecord;
import de.se.cashregistersystem.repository.PledgeRepository;
import de.se.cashregistersystem.repository.ProductRepository;
import de.se.cashregistersystem.repository.ProductTransactionRepository;
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
    private ProductTransactionRepository productTransactionRepository;

    @Autowired
    private TransactionRecordService service;
    @Autowired
    private PayPalService paypalService;
    @Autowired
    private PrintingService printingService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PledgeRepository pledgeRepository;

    @GetMapping("/{id}")
    public ResponseEntity<TransactionRecord> getTransactionById(@PathVariable UUID id){

        Optional<TransactionRecord> transaction = transactionRecordRepository.findById(id);
        if (!transaction.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found.");
        }
        return new ResponseEntity<>(transaction.get(), HttpStatus.OK);
    }
    @PostMapping("/create")
    public ResponseEntity<Object> create(@RequestBody TransactionRequestDTO requestDTO){

            UUID transactionRecordId = service.createTransactionRecord(requestDTO.getItems(), requestDTO.getPledges());
            return new ResponseEntity<>(transactionRecordId, HttpStatus.CREATED);
    }
    @PostMapping("/complete")
    public ResponseEntity<String> completeTransaction(@RequestBody CompleteTransactionDTO body) {
        String orderId = body.getOrderId();


        if (orderId == null || orderId.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Order ID cannot be null or empty"
            );
        }
            UUID transactionId = paypalService.verifyPayment(orderId);

            // Get items for transaction
            Optional<List<UUID>> productIds = productTransactionRepository.getProductsByTransactionId(transactionId);
            List<Pledge> pledges = pledgeRepository.findPledgesByTransactionId(transactionId).get();
            if (productIds.isEmpty() && pledges.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No items found for transaction: " + transactionId
                );
            }
            List<Product> products = productRepository.findAllById(productIds.get());

            printingService.printReceipt(products, pledges);

            return new ResponseEntity<String>("Transaction completed", HttpStatus.OK);
    }
    @PostMapping("/scan/{barcode_id}")
    public ResponseEntity<Object> scanTransaction(@PathVariable String barcode_id){
        Optional<TransactionRecord> transactionRecord = transactionRecordRepository.findPaidTransactionRecord(barcode_id);
        if(!transactionRecord.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No paid Transaction Record");
        }
        TransactionRecord record = transactionRecord.get();
        record.setStatus("scanned");
        transactionRecordRepository.save(record);
        return new ResponseEntity<>("Transaction successfully scanned", HttpStatus.OK);
    }
}
