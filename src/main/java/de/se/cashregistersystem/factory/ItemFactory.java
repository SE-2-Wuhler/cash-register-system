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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class ItemFactory {

    @Autowired
    BrandRepository brandRepository;
    @Autowired
    ItemRepository itemRepository;

    public Item create(String name, String barcodeId, String brandName, boolean fluid, double price, String category) {
        // Validate input parameters
        if (name == null || name.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Item name cannot be null or empty"
            );
        }
        if (barcodeId == null || barcodeId.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Barcode ID cannot be null or empty"
            );
        }
        try {
            // Check for existing item
            Optional<Item> item = itemRepository.findItemByBarcodeId(barcodeId);
            if (item.isPresent()) {
                Item currentItem = item.get();
                if (currentItem.getPrice() != price) {
                    currentItem.setPrice(price);

                    return itemRepository.save(currentItem);
                }
                return currentItem;
            }

            // Create new item
            Item itemToSave = new Item();
            itemToSave.setName(name);
            itemToSave.setBarcodeId(barcodeId);
            itemToSave.setCategory(category);
            itemToSave.setPrice(price);

            // Handle brand
            Optional<Brand> existingBrand = brandRepository.findBrandByName(brandName);
            Brand brand;
            if (existingBrand.isEmpty()) {
                try {
                    brand = brandRepository.save(new Brand(brandName, ""));
                } catch (Exception e) {
                    throw new ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to create new brand: " + e.getMessage()
                    );
                }
            } else {
                brand = existingBrand.get();
            }

            itemToSave.setBrandId(brand.getId());

            // Set pledge value for fluid items
            if (fluid) {
                itemToSave.setPledgeValue(0.25);
            }

            // Set default nutriscore
            itemToSave.setNutriscore('a');

            // Save and return the new item
            try {
                return itemRepository.save(itemToSave);
            } catch (Exception e) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Failed to save item: " + e.getMessage()
                );
            }

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unexpected error during item creation: " + e.getMessage()
            );
        }
    }
}