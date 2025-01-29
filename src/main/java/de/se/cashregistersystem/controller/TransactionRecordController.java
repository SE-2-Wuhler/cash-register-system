package de.se.cashregistersystem.controller;

import de.se.cashregistersystem.dto.CompleteOrderResponseDTO;
import de.se.cashregistersystem.dto.CompleteTransactionDTO;
import de.se.cashregistersystem.dto.TransactionRequestDTO;
import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.entity.Product;
import de.se.cashregistersystem.entity.TransactionRecord;
import de.se.cashregistersystem.repository.*;
import de.se.cashregistersystem.service.PayPalService;
import de.se.cashregistersystem.service.PrintingService;
import de.se.cashregistersystem.service.TransactionRecordService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/transaction")
@Tag(name = "Transaction", description = "The Transaction API for managing sales transactions")
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

    @Operation(
            summary = "Get transaction by ID",
            description = "Retrieves a specific transaction record using its UUID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transaction found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionRecord.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transaction not found",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<TransactionRecord> getTransactionById(
            @Parameter(description = "UUID of the transaction to retrieve")
            @PathVariable UUID id) {
        Optional<TransactionRecord> transaction = transactionRecordRepository.findById(id);
        if (!transaction.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found.");
        }
        return new ResponseEntity<>(transaction.get(), HttpStatus.OK);
    }

    @Operation(
            summary = "Create a new transaction",
            description = "Creates a new transaction record with the specified items and pledges"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Transaction created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UUID.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content
            )
    })
    @PostMapping("/create")
    public ResponseEntity<Object> create(
            @Parameter(
                    description = "Transaction request containing items and pledges",
                    required = true,
                    schema = @Schema(implementation = TransactionRequestDTO.class)
            )
            @RequestBody TransactionRequestDTO requestDTO) {
        UUID transactionRecordId = service.createTransactionRecord(requestDTO.getItems(), requestDTO.getPledges());
        return new ResponseEntity<>(transactionRecordId, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Complete a transaction",
            description = "Completes a transaction by verifying payment, processing items, and printing receipt"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transaction completed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid order ID",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No items found for transaction",
                    content = @Content
            )
    })
    @PostMapping("/complete")
    public ResponseEntity<CompleteOrderResponseDTO> completeTransaction(
            @Parameter(
                    description = "Transaction completion request containing PayPal order ID",
                    required = true,
                    schema = @Schema(implementation = CompleteTransactionDTO.class)
            )
            @RequestBody CompleteTransactionDTO body) {
        String orderId = body.getOrderId();

        if (orderId == null || orderId.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Order ID cannot be null or empty"
            );
        }
        UUID transactionId = paypalService.verifyPayment(orderId);

        Optional<List<UUID>> productIds = productTransactionRepository.getProductsByTransactionId(transactionId);
        List<Pledge> pledges = pledgeRepository.findPledgesByTransactionId(transactionId).get();
        if (productIds.isEmpty() && pledges.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No items found for transaction: " + transactionId
            );
        }
        List<Product> products = productRepository.findAllById(productIds.get());
        service.complete(transactionId);
        printingService.printReceipt(products, pledges);

        return new ResponseEntity<CompleteOrderResponseDTO>(new CompleteOrderResponseDTO("Transaction completed"), HttpStatus.OK);
    }
}