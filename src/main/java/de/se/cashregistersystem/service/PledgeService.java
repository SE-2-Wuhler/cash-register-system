package de.se.cashregistersystem.service;

import de.se.cashregistersystem.dto.PledgeDTO;
import de.se.cashregistersystem.dto.PledgeItemDTO;
import de.se.cashregistersystem.entity.Item;
import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.factory.PledgeFactory;
import de.se.cashregistersystem.repository.ItemRepository;
import de.se.cashregistersystem.repository.PledgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Pledge createPledge(PledgeDTO pledge) {
            printingService.printPledgeReceipt(pledge);
        try {
            return pledgeRepository.save(pledgeFactory.create(pledge));
        } catch (Exception e) {
            throw new RuntimeException("Insert of pledge failed "+ e);
        }


    }
    public List<Item> getAllPledgeItems(){
        return itemRepository.findItemsWithPositivePledgeValue();
    }
}
