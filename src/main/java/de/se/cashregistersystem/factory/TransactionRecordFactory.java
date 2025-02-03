package de.se.cashregistersystem.factory;

import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.entity.Product;
import de.se.cashregistersystem.entity.TransactionRecord;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
@Service
public class TransactionRecordFactory {


    public TransactionRecord create(List<Product> products, List<Pledge> pledges){
        BigDecimal productsAmount = calculateProductAmount(products);
        BigDecimal pledgesAmount = calculatePledgeAmount(pledges);
        BigDecimal totalAmount = productsAmount.subtract(pledgesAmount);
        TransactionRecord newTransactionRecord = new TransactionRecord(totalAmount);
        newTransactionRecord.setStatus("unpaid");
        return newTransactionRecord;
    }

    private BigDecimal calculateProductAmount(List<Product> products){
        double amount = 0;

        for (Product product : products){
            amount += product.getPrice() + product.getPledgeValue();
        }
        return BigDecimal.valueOf(amount);
    }
    private BigDecimal calculatePledgeAmount(List<Pledge> pledges){
        double amount = 0;

        for (Pledge pledge : pledges){
            amount += pledge.getValue();
        }
        return BigDecimal.valueOf(amount);
    }
}



