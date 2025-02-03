package de.se.cashregistersystem.service;

import com.google.common.annotations.VisibleForTesting;
import de.se.cashregistersystem.dto.CompleteTransactionDTO;
import de.se.cashregistersystem.dto.ProductWithQuantityDTO;
import de.se.cashregistersystem.entity.*;
import de.se.cashregistersystem.factory.ProductTransactionFactory;
import de.se.cashregistersystem.factory.TransactionRecordFactory;
import de.se.cashregistersystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private ProductTransactionFactory productTransactionFactory;
    @Autowired
    private PledgeRepository pledgeRepository;
    @Autowired
    private TransactionRecordFactory transactionRecordFactory;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PrintingService printingService;


    public TransactionRecord getTransactionRecord(UUID transactionId) {

        Optional<TransactionRecord> transaction = transactionRecordRepository.findById(transactionId);
        if (!transaction.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found.");
        }
        return transaction.get();

    }

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
            ProductTransaction productTransaction = productTransactionFactory.create(transactionId, product.getId());
            productTransactionRepository.save(productTransaction);
        }

        return transactionId;
    }
    public void completeTransaction(CompleteTransactionDTO body) {
        String orderId = body.getOrderId();

        if (orderId == null || orderId.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Order ID cannot be null or empty"
            );
        }
        UUID transactionId = paymentService.processPayment(orderId);

        Optional<List<UUID>> productIds = productTransactionRepository.getProductsByTransactionId(transactionId);
        Optional<List<Pledge>> pledges = pledgeRepository.findPledgesByTransactionId(transactionId);
        if (productIds.isEmpty() || pledges.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No items found for transaction: " + transactionId
            );
        }
        List<Product> products = new ArrayList<>();
        for ( UUID productId : productIds.get()){
            Optional<Product> product = productRepository.findById(productId);
            if(product.isEmpty()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not find product with id: " + product);
            }
            products.add(product.get());
        }


        String receiptBarcodeId = printingService.printReceipt(products, pledges.get());
        complete(transactionId, receiptBarcodeId);

    }
    public void scan(String barcodeId) {
        Optional<TransactionRecord> transactionRecord = transactionRecordRepository.findPaidTransactionRecord(barcodeId);
        if(!transactionRecord.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No paid Transaction Record");
        }
        TransactionRecord record = transactionRecord.get();
        record.setStatus("scanned");
        transactionRecordRepository.save(record);
    }

    public void complete(UUID transactionId, String receiptBarcodeId){
        TransactionRecord transactionRecord = transactionRecordRepository.findById(transactionId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not find transaction with id: " + transactionId));
        transactionRecord.setStatus("paid");
        transactionRecord.setBarcodeId(receiptBarcodeId);
        transactionRecordRepository.save(transactionRecord);
    }
    @Transactional
    public void cancel(UUID transactionId){
        pledgeRepository.findPledgesByTransactionId(transactionId).get().forEach(pledge -> {
            pledge.setTransactionId(null);
            pledgeRepository.save(pledge);
        });
        productTransactionRepository.deleteByTransactionRecordId(transactionId);
        transactionRecordRepository.deleteById(transactionId);

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
            if(currentPledge.getTransactionId() == null){
                pledgesList.add(currentPledge);
            }
        }
        return pledgesList;
    }

}