package de.se.cashregistersystem.factory;

import de.se.cashregistersystem.dto.ItemDTO;
import de.se.cashregistersystem.dto.PledgeDTO;
import de.se.cashregistersystem.entity.Item;
import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.repository.BrandRepository;

public class ItemFactory {

    BrandRepository brandRepository;

    public Item create(ItemDTO itemDTO) {

        return new Item(
                itemDTO.getName(),
                itemDTO.getBarcodeId(),
                brandRepository.getReferenceById(itemDTO.getBrandId()),
                itemDTO.getDescription(),
                itemDTO.getNutriscore(),
                itemDTO.getImgUrl(),
                itemDTO.getPrice(),
                itemDTO.getPledgeValue(),
                itemDTO.isNonScanable(),
                itemDTO.getCategory()
        );
    }
}
