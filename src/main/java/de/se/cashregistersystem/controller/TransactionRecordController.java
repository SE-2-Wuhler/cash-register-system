package de.se.cashregistersystem.controller;

import de.se.cashregistersystem.dto.ItemDTO;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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

    @PostMapping("/create")
    public ResponseEntity<UUID> create(@RequestBody TransactionRequestDTO requestDTO){

        UUID transactionRecord = service.createTransactionRecord(requestDTO.getItems(),requestDTO.getPledges());
        return new ResponseEntity<UUID>(transactionRecord , HttpStatus.CREATED);
    }
    @PostMapping("/complete")
    public ResponseEntity<String> completeTransaction(@RequestBody String orderId){

        try {
            UUID transactionId = payPalService.verifyPayment(orderId);
            List<UUID> ids = itemTransactionRepository.getItemsByTransactionId(UUID.fromString("53c597e0-9f69-4d18-aacd-d79d27b93e5e"));
            List<Item> items = itemRepository.findAllById(ids);
            printingService.printReceipt(items);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }



        return new ResponseEntity<String>(HttpStatus.OK);
    }
}
