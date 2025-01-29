package de.se.cashregistersystem.factory;

import de.se.cashregistersystem.entity.Brand;
import de.se.cashregistersystem.entity.Product;
import de.se.cashregistersystem.repository.BrandRepository;
import de.se.cashregistersystem.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductFactoryTest {

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductFactory productFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_withValidParameters_returnsProduct() {
        String name = "Product Name";
        String barcodeId = "123456789";
        String brandName = "Brand Name";
        double pledgeValue = 10.0;
        double price = 20.0;
        String category = "Category";
        char nutriscore = 'A';
        String imgUrl = "http://example.com/image.jpg";

        when(productRepository.findProductByBarcodeId(barcodeId)).thenReturn(Optional.empty());
        when(brandRepository.findBrandByName(brandName)).thenReturn(Optional.empty());
        when(brandRepository.save(any(Brand.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product product = productFactory.create(name, barcodeId, brandName, pledgeValue, price, category, nutriscore, imgUrl);

        assertNotNull(product);
        assertEquals(name, product.getName());
        assertEquals(barcodeId, product.getBarcodeId());
        assertEquals(pledgeValue, product.getPledgeValue());
        assertEquals(price, product.getPrice());
        assertEquals(category, product.getCategory());
        assertEquals(nutriscore, product.getNutriscore());
        assertEquals(imgUrl, product.getImgUrl());
    }

    @Test
    void create_withExistingProduct_updatesProduct() {
        String name = "Product Name";
        String barcodeId = "123456789";
        String brandName = "Brand Name";
        double pledgeValue = 10.0;
        double price = 20.0;
        String category = "Category";
        char nutriscore = 'A';
        String imgUrl = "http://example.com/image.jpg";

        Product existingProduct = new Product();
        existingProduct.setBarcodeId(barcodeId);

        when(productRepository.findProductByBarcodeId(barcodeId)).thenReturn(Optional.of(existingProduct));

        Product product = productFactory.create(name, barcodeId, brandName, pledgeValue, price, category, nutriscore, imgUrl);

        assertNotNull(product);
        assertEquals(existingProduct, product);
        assertEquals(price, product.getPrice());
        assertEquals(pledgeValue, product.getPledgeValue());
    }

    @Test
    void create_withNullName_throwsResponseStatusException() {
        String barcodeId = "123456789";
        String brandName = "Brand Name";
        double pledgeValue = 10.0;
        double price = 20.0;
        String category = "Category";
        char nutriscore = 'A';
        String imgUrl = "http://example.com/image.jpg";

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            productFactory.create(null, barcodeId, brandName, pledgeValue, price, category, nutriscore, imgUrl);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Product name cannot be null or empty", exception.getReason());
    }

    @Test
    void create_withEmptyBarcodeId_throwsResponseStatusException() {
        String name = "Product Name";
        String brandName = "Brand Name";
        double pledgeValue = 10.0;
        double price = 20.0;
        String category = "Category";
        char nutriscore = 'A';
        String imgUrl = "http://example.com/image.jpg";

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            productFactory.create(name, "", brandName, pledgeValue, price, category, nutriscore, imgUrl);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Barcode ID cannot be null or empty", exception.getReason());
    }
}