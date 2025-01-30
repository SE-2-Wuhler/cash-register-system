package de.se.cashregistersystem.repository;

import de.se.cashregistersystem.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    @Query("SELECT i FROM Product i WHERE i.pledgeValue > 0")
    Optional<List<Product>> findProductsWithPositivePledgeValue();

    Optional<Product> findProductByBarcodeId(String barcode_id);

    Optional<List<Product>> findAllByIsNonScanableTrue();
}
