package de.se.cashregistersystem.controller;

import de.se.cashregistersystem.entity.TransactionRecord;
import de.se.cashregistersystem.repository.ItemTransactionRepository;
import de.se.cashregistersystem.repository.PledgeRepository;
import de.se.cashregistersystem.repository.PledgeTransactionRepository;
import de.se.cashregistersystem.repository.TransactionRecordRepository;
import de.se.cashregistersystem.service.PledgeService;
import de.se.cashregistersystem.service.TransactionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class TransactionRecordController {

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Autowired
    private ItemTransactionRepository itemTransactionRepository;

    @Autowired
    private PledgeTransactionRepository pledgeTransactionRepository;

    @Autowired
    private TransactionRecordService service;

    @PostMapping("/transaction/create")
    public ResponseEntity<String> createPledge(@PathVariable UUID[] items, @PathVariable UUID[] pledges){

        TransactionRecord transactionRecord = service.createTransactionRecord(items,pledges);
        return new ResponseEntity<String>(" Created Transaction: " + transactionRecord.getId() , HttpStatus.CREATED);
    }

}
