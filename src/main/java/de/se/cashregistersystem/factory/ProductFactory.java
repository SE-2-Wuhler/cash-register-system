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
public class ProductFactory {

    @Autowired
    BrandRepository brandRepository;
    @Autowired
    ProductRepository productRepository;

    public Product create(String name, String barcodeId, String brandName, double pledgeValue, double price, String category, char nutriscore, String imgUrl) {
        // Validate input parameters
        if (name == null || name.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product name cannot be null or empty"
            );
        }
        if (barcodeId == null || barcodeId.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Barcode ID cannot be null or empty"
            );
        }

        // Check for existing item
        Optional<Product> item = productRepository.findProductByBarcodeId(barcodeId);
        if (item.isPresent()) {
            Product currentProduct = item.get();
            currentProduct.setPrice(price);
            currentProduct.setPledgeValue(pledgeValue);
            return currentProduct;
        }

        // Create new item
        Product newProduct = new Product();
        newProduct.setName(name);
        newProduct.setBarcodeId(barcodeId);
        newProduct.setCategory(category);
        newProduct.setPrice(price);
        newProduct.setPledgeValue(pledgeValue);
        newProduct.setNutriscore(nutriscore);
        newProduct.setImgUrl(imgUrl);

        // Handle brand
        Optional<Brand> existingBrand = brandRepository.findBrandByName(brandName);
        Brand brand;
        brand = existingBrand.orElseGet(() -> brandRepository.save(new Brand(brandName, "")));

        newProduct.setBrandId(brand.getId());
        return newProduct;
    }

}