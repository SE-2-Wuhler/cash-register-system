package de.se.cashregistersystem.service;

import de.se.cashregistersystem.dto.ItemDTO;
import de.se.cashregistersystem.dto.ItemWithQuantityDTO;
import de.se.cashregistersystem.dto.PledgeDTO;
import de.se.cashregistersystem.entity.*;
import de.se.cashregistersystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionRecordService {
    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Autowired
    private ItemTransactionRepository itemTransactionRepository;

    @Autowired
    private PledgeTransactionRepository pledgeTransactionRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private PledgeRepository pledgeRepository;

    public UUID createTransactionRecord(ItemWithQuantityDTO[] items, UUID[] pledges) {

        List<Item> allItems = new ArrayList<>();
        List<Pledge> allPledges = new ArrayList<>();
        double totalAmount = 0.0;
        if(items.length > 0) {
            Optional<Item> currItem;
            for(ItemWithQuantityDTO item : items) {
                UUID id = item.getItemId();
                currItem = itemRepository.findById(item.getItemId());
                if(currItem.isEmpty()){
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid ItemID: " + id);
                }
                allItems.add(currItem.get());
                totalAmount += (currItem.get().getPrice() * item.getQuantity());
                totalAmount += (currItem.get().getPledgeValue() * item.getQuantity());
            }
        }

        if(pledges.length > 0) {
            Pledge currPledge;
            for(UUID pledge: pledges){
                currPledge = pledgeRepository.getReferenceById(pledge);
                allPledges.add(currPledge);
                totalAmount -= currPledge.getValue();
            }
        }

        TransactionRecord transactionRecord = new TransactionRecord();
        transactionRecord.setTotalAmount(BigDecimal.valueOf(totalAmount));
        transactionRecord.setStatus("unpaid");
        transactionRecord = transactionRecordRepository.save(transactionRecord);

        for (Item item : allItems) {
            ItemTransaction itemTransaction = new ItemTransaction();
            itemTransaction.setTransactionRecord(transactionRecord.getId());
            itemTransaction.setItem(item.getId());
            itemTransactionRepository.save(itemTransaction);
        }
        for (Pledge pledge : allPledges) {
            PledgeTransaction pledgeTransaction = new PledgeTransaction();
            pledgeTransaction.setTransaction(transactionRecord.getId());
            pledgeTransaction.setPledge(pledge.getId());
            pledgeTransactionRepository.save(pledgeTransaction);
        }

        return transactionRecord.getId();
    }

}