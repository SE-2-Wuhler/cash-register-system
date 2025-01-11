package de.se.cashregistersystem.service;

import de.se.cashregistersystem.entity.*;
import de.se.cashregistersystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    public TransactionRecord createTransactionRecord(UUID[] items, UUID[] pledges) {

        List<Item> allItems = new ArrayList<>();
        List<Pledge> allPledges = new ArrayList<>();
        double totalAmount = 0.0;
        if(items.length > 0) {
            Item currItem;
            for(UUID item : items) {
                currItem = itemRepository.getReferenceById(item);
                allItems.add(currItem);
                totalAmount += currItem.getPrice();
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
        transactionRecord.setTotalAmount(totalAmount);
        transactionRecord.setStatus("unpaid");
        transactionRecord = transactionRecordRepository.save(transactionRecord);

        for (Item item : allItems) {
            ItemTransaction itemTransaction = new ItemTransaction();
            itemTransaction.setTransactionRecord(transactionRecord);
            itemTransaction.setItem(item);
            itemTransactionRepository.save(itemTransaction);
        }
        for (Pledge pledge : allPledges) {
            PledgeTransaction pledgeTransaction = new PledgeTransaction();
            pledgeTransaction.setTransaction(transactionRecord);
            pledgeTransaction.setPledge(pledge);
            pledgeTransactionRepository.save(pledgeTransaction);
        }

        return transactionRecord;
    }
}