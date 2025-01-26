package de.se.cashregistersystem.factory;

import de.se.cashregistersystem.dto.PledgeDTO;
import de.se.cashregistersystem.dto.ProductWithQuantityDTO;
import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.entity.Product;
import de.se.cashregistersystem.repository.ProductRepository;
import de.se.cashregistersystem.util.POSBarcode;
import org.apache.el.util.ReflectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class PledgeFactory {
    @Autowired
    ProductRepository productRepository;
    public static class InvalidPledgeException extends Exception{

        public InvalidPledgeException(String message){
            super(message);
        }

    }
    public Pledge create(ProductWithQuantityDTO[] products) throws InvalidPledgeException {
        double value = calculateValue(products);
        if(value <= 0){
            throw new InvalidPledgeException("Invalid pledge value : " + value +  " <= 0" );
        }
        return new Pledge(value);
    }

    private double calculateValue(ProductWithQuantityDTO[] items){
        double value = 0;
        for ( ProductWithQuantityDTO item: items) {

            Optional<Product> productOpt = productRepository.findById(item.getItemId());
            if(productOpt.isEmpty()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: " + item.getItemId());
            }
            Product product  = productOpt.get();

            value += product.getPledgeValue() * item.getQuantity();
        }
        return value;
    }

}