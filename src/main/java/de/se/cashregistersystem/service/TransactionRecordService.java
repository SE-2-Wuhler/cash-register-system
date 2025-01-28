package de.se.cashregistersystem.service;

import com.google.common.annotations.VisibleForTesting;
import de.se.cashregistersystem.dto.ProductWithQuantityDTO;
import de.se.cashregistersystem.entity.*;
import de.se.cashregistersystem.factory.TransactionRecordFactory;
import de.se.cashregistersystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionRecordService {
    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Autowired
    private ProductTransactionRepository productTransactionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PledgeRepository pledgeRepository;
    @Autowired
    private TransactionRecordFactory transactionRecordFactory;
    @Transactional
    public UUID createTransactionRecord(ProductWithQuantityDTO[] products, UUID[] pledges) {
        if ((products == null || products.length == 0) && (pledges == null || pledges.length == 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Items or pledges can not be empty");
        }

        List<Product> productsList = getProductsList(products);
        List<Pledge> pledgesList = getPledgesList(pledges);

        TransactionRecord newTransactionRecord = transactionRecordFactory.create(productsList,pledgesList);

        UUID transactionId = transactionRecordRepository.save(newTransactionRecord).getId();
        for (Pledge pledge : pledgesList){
            pledge.setTransactionId(transactionId);
            pledgeRepository.save(pledge);
        }

        for (Product product : productsList){
            ProductTransaction productTransaction = new ProductTransaction(transactionId, product.getId());
            productTransactionRepository.save(productTransaction);
        }

        return transactionId;
    }

    public void complete(UUID transactionId){
        TransactionRecord transactionRecord = transactionRecordRepository.findById(transactionId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not find transaction with id: " + transactionId));
        transactionRecord.setStatus("paid");
        transactionRecordRepository.save(transactionRecord);
    }

    private List<Product> getProductsList(ProductWithQuantityDTO[] products){
        List<Product> productsList = new ArrayList<>();
        for ( ProductWithQuantityDTO product : products){
            Product currentProduct = productRepository.findById(product.getItemId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid ItemID: " + product.getItemId()));
            for (int i = 0; i<product.getQuantity(); i++){
                productsList.add(currentProduct);
            }
        }
        return productsList;
    }
    @VisibleForTesting()
    private List<Pledge> getPledgesList(UUID[] pledges){
        List<Pledge> pledgesList = new ArrayList<>();
        for ( UUID pledgeId : pledges){
            Pledge currentPledge = pledgeRepository.findById(pledgeId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid ItemID: " + pledgeId));
            if(currentPledge.getTransactionId() != null){
                pledgesList.add(currentPledge);
            }
        }
        return pledgesList;
    }

}