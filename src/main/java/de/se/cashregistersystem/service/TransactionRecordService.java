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

        try {
            validateInput(items, pledges);
            BigDecimal itemAmount = processItems(items, allItems);
            BigDecimal pledgeAmount = processPledges(pledges, allPledges);

            TransactionRecord transactionRecord = createAndSaveTransactionRecord(itemAmount.subtract(pledgeAmount));
            saveItemTransactions(transactionRecord, allItems);
            savePledgeTransactions(transactionRecord, allPledges);

            return transactionRecord.getId();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating transaction: " + e.getMessage(), e);
        }
    }

    private void validateInput(ItemWithQuantityDTO[] items, UUID[] pledges) {
        if ((items == null || items.length == 0) && (pledges == null || pledges.length == 0)) {
            throw new IllegalArgumentException("At least one item or pledge must be specified.");
        }
    }

    private BigDecimal processItems(ItemWithQuantityDTO[] items, List<Item> allItems) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (items != null) {
            for (ItemWithQuantityDTO item : items) {
                Item currItem = itemRepository.findById(item.getItemId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid ItemID: " + item.getItemId()));

                if (item.getQuantity() <= 0) {
                    throw new IllegalArgumentException("Invalid quantity for Item: " + item.getItemId());
                }

                allItems.add(currItem);
                double itemPrice = (currItem.getPrice() * item.getQuantity()) + (currItem.getPledgeValue() * item.getQuantity());
                totalAmount = totalAmount.add(BigDecimal.valueOf(itemPrice));
            }
        }
        return totalAmount;
    }

    private BigDecimal processPledges(UUID[] pledges, List<Pledge> allPledges) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (pledges != null) {
            for (UUID pledgeId : pledges) {
                Pledge currPledge = pledgeRepository.findById(pledgeId)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid PledgeID: " + pledgeId));
                currPledge.setValidated(true);
                Pledge actPledge = pledgeRepository.save(currPledge);

                allPledges.add(actPledge);
                totalAmount = totalAmount.subtract(BigDecimal.valueOf(currPledge.getValue()));
            }
        }
        return totalAmount;
    }

    private TransactionRecord createAndSaveTransactionRecord(BigDecimal totalAmount) {
        TransactionRecord transactionRecord = new TransactionRecord();
        transactionRecord.setTotalAmount(totalAmount);
        transactionRecord.setStatus("unpaid");
        return transactionRecordRepository.save(transactionRecord);
    }

    private void saveItemTransactions(TransactionRecord transactionRecord, List<Item> allItems) {
        for (Item item : allItems) {
            ItemTransaction itemTransaction = new ItemTransaction();
            itemTransaction.setTransactionRecord(transactionRecord.getId());
            itemTransaction.setItem(item.getId());
            itemTransactionRepository.save(itemTransaction);
        }
    }

    private void savePledgeTransactions(TransactionRecord transactionRecord, List<Pledge> allPledges) {
        for (Pledge pledge : allPledges) {
            PledgeTransaction pledgeTransaction = new PledgeTransaction();
            pledgeTransaction.setTransaction(transactionRecord.getId());
            pledgeTransaction.setPledge(pledge.getId());
            pledgeTransactionRepository.save(pledgeTransaction);
        }
    }
}