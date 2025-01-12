package de.se.cashregistersystem.controller;

import de.se.cashregistersystem.entity.Item;
import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.repository.ItemRepository;
import de.se.cashregistersystem.repository.PledgeRepository;
import de.se.cashregistersystem.util.Scanable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private PledgeRepository pledgeRepository;
    @GetMapping("/{barcode_id}")
    public ResponseEntity<Scanable> getById(@PathVariable("barcode_id") String barcodeid){

        Optional<Item> item = itemRepository.findItemByBarcodeId(barcodeid);
        if(item.isPresent()){
            return new ResponseEntity<Scanable>(item.get(), HttpStatus.OK);
        }
        Optional<Pledge> pledge = pledgeRepository.findPledgeByBarcodeId(barcodeid);
        if(pledge.isPresent()){
            return new ResponseEntity<Scanable>(pledge.get(), HttpStatus.OK);
        }
        throw new RuntimeException (HttpStatus.INTERNAL_SERVER_ERROR.toString());
    }


}
