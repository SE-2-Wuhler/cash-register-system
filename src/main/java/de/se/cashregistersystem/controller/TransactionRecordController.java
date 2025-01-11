package de.se.cashregistersystem.controller;

import de.se.cashregistersystem.dto.ItemWithQuantityDTO;
import de.se.cashregistersystem.dto.TransactionRequestDTO;
import de.se.cashregistersystem.entity.TransactionRecord;
import de.se.cashregistersystem.repository.ItemTransactionRepository;
import de.se.cashregistersystem.repository.PledgeTransactionRepository;
import de.se.cashregistersystem.repository.TransactionRecordRepository;
import de.se.cashregistersystem.service.TransactionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public ResponseEntity<UUID> createPledge(@RequestBody TransactionRequestDTO requestDTO){

        UUID transactionRecord = service.createTransactionRecord(requestDTO.getItems(),requestDTO.getPledges());
        return new ResponseEntity<UUID>(transactionRecord , HttpStatus.CREATED);
    }
}
