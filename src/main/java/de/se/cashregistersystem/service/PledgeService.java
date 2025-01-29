package de.se.cashregistersystem.service;

import de.se.cashregistersystem.dto.ProductWithQuantityDTO;
import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.entity.Product;
import de.se.cashregistersystem.factory.PledgeFactory;
import de.se.cashregistersystem.repository.ProductRepository;
import de.se.cashregistersystem.repository.PledgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

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
    private ProductRepository productRepository;

    public UUID createPledge(ProductWithQuantityDTO[] products) {

        try {
            Pledge newPledge = pledgeFactory.create(products);
            String barcode_id = printingService.printPledgeReceipt(newPledge);
            newPledge.setBarcodeId(barcode_id);
            return pledgeRepository.save(newPledge).getId();
        } catch (PledgeFactory.InvalidPledgeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    public List<Product> getAllPledgeItems(){

            Optional<List<Product>> productsOpt = productRepository.findProductsWithPositivePledgeValue();
            if(productsOpt.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Items with positive Pledge Values found");
            }
            return productsOpt.get();
    }
}
