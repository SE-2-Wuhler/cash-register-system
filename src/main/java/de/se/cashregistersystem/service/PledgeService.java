package de.se.cashregistersystem.service;


import de.se.cashregistersystem.dto.ItemWithQuantityDTO;
import de.se.cashregistersystem.dto.PledgeDTO;
import de.se.cashregistersystem.entity.Item;
import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.factory.PledgeFactory;
import de.se.cashregistersystem.repository.ItemRepository;
import de.se.cashregistersystem.repository.PledgeRepository;
import de.se.cashregistersystem.util.POSBarcode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PledgeService {

    @Autowired
    private PledgeRepository pledgeRepository;
    @Autowired
    private PrintingService printingService;
    @Autowired
    private PledgeFactory pledgeFactory;
    @Autowired
    private ItemRepository itemRepository;

    public UUID createPledge(ItemWithQuantityDTO[] items) {
        double value = this.calculateValue(items);
        String barcode_id = printingService.printValueReceipt(value);
        try {
             return pledgeRepository.save(pledgeFactory.create(barcode_id,value)).getId();
        } catch (Exception e) {
            throw new RuntimeException("Insert of pledge failed "+ e);
        }


    }
    private double calculateValue(ItemWithQuantityDTO[] items){
        double value = 0;
        for ( ItemWithQuantityDTO item: items
             ) {
            Item i = itemRepository.findById(item.getItemId()).get();
            value+= i.getPledgeValue() * item.getQuantity();
        }
        return value;
    }
}
