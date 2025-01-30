package de.se.cashregistersystem.controller;

import de.se.cashregistersystem.dto.CompleteOrderResponseDTO;
import de.se.cashregistersystem.dto.CompleteTransactionDTO;
import de.se.cashregistersystem.dto.TransactionRequestDTO;
import de.se.cashregistersystem.entity.TransactionRecord;
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

import java.util.*;

@RestController
@RequestMapping("/transaction")
@Tag(name = "Transaction", description = "The Transaction API for managing sales transactions")
public class TransactionRecordController {

    @Autowired
    private TransactionRecordService service;

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
        TransactionRecord transaction = service.getTransactionRecord(id);
        return new ResponseEntity<>(transaction, HttpStatus.OK);
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
        service.completeTransaction(body);
        return new ResponseEntity<>(new CompleteOrderResponseDTO("Transaction completed"), HttpStatus.OK);
    }
    @Operation(
            summary = "Cancel a transaction",
            description = "Cancels a transaction with the specified ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transaction cancelled successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CompleteOrderResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transaction not found",
                    content = @Content
            )
    })
    @PostMapping("/cancel/{id}")
    public ResponseEntity<CompleteOrderResponseDTO> cancel(
            @Parameter(description = "UUID of the transaction to cancel")
            @PathVariable UUID id) {
        service.cancel(id);
        return new ResponseEntity<CompleteOrderResponseDTO>(
                new CompleteOrderResponseDTO("Transaction cancelled"),
                HttpStatus.OK
        );
    }

}