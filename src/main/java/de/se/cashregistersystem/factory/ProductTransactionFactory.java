package de.se.cashregistersystem.factory;

import de.se.cashregistersystem.entity.ProductTransaction;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductTransactionFactory {

   public ProductTransaction create(UUID transactionRecordId, UUID productId) {
        return new ProductTransaction(transactionRecordId, productId);
    }

}
