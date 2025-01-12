package de.se.cashregistersystem.factory;

import de.se.cashregistersystem.dto.ItemDTO;
import de.se.cashregistersystem.dto.PledgeDTO;
import de.se.cashregistersystem.entity.Brand;
import de.se.cashregistersystem.entity.Item;
import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.repository.BrandRepository;
import de.se.cashregistersystem.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ItemFactory {

    @Autowired
    BrandRepository brandRepository;
    @Autowired
    ItemRepository itemRepository;

    public Item create(String name, String barcodeId, String brandName, boolean fluid, double price, String category) {
        Optional<Item> item = itemRepository.findItemByBarcodeId(barcodeId);
        if(item.isPresent()){
            Item currentItem = item.get();
            if(currentItem.getPrice() != price){
                currentItem.setPrice(price);
                return currentItem;
            }
        }

        Item itemToSave = new Item();
        itemToSave.setName(name);
        itemToSave.setBarcodeId(barcodeId);
        itemToSave.setCategory(category);
        itemToSave.setPrice(price);
        Optional<Brand> existingBrand = brandRepository.findBrandByName(brandName);
        if(!existingBrand.isPresent()){
            Brand createdBrand = brandRepository.save(new Brand(brandName,""));
            itemToSave.setBrandId(createdBrand.getId());
        }
        itemToSave.setBrandId(existingBrand.get().getId());
        if(fluid){
            itemToSave.setPledgeValue(0.25);
        }
        itemToSave.setNutriscore('a');



        return itemToSave;
    }
}
