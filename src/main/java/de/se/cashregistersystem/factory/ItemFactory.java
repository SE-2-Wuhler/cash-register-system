package de.se.cashregistersystem.factory;

import de.se.cashregistersystem.entity.Brand;
import de.se.cashregistersystem.entity.Product;
import de.se.cashregistersystem.repository.BrandRepository;
import de.se.cashregistersystem.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class ItemFactory {

    @Autowired
    BrandRepository brandRepository;
    @Autowired
    ProductRepository productRepository;

    public Product create(String name, String barcodeId, String brandName, boolean fluid, double price, String category) {
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
            Optional<Product> item = productRepository.findItemByBarcodeId(barcodeId);
            if (item.isPresent()) {
                Product currentProduct = item.get();
                currentProduct.setPrice(price);
                return currentProduct;
            }

            // Create new item
            Product productToSave = new Product();
            productToSave.setName(name);
            productToSave.setBarcodeId(barcodeId);
            productToSave.setCategory(category);
            productToSave.setPrice(price);

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

            productToSave.setBrandId(brand.getId());

            // Set pledge value for fluid items
            if (fluid) {
                productToSave.setPledgeValue(0.25);
            }

            // Set default nutriscore
            productToSave.setNutriscore('a');

            // Save and return the new item
            try {
                return productRepository.save(productToSave);
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