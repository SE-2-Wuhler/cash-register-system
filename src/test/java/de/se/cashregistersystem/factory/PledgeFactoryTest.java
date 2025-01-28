package de.se.cashregistersystem.factory;

import de.se.cashregistersystem.dto.ProductWithQuantityDTO;
import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.entity.Product;
import de.se.cashregistersystem.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PledgeFactoryTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private PledgeFactory pledgeFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_withValidProducts_returnsPledge() throws PledgeFactory.InvalidPledgeException {
        ProductWithQuantityDTO productDTO = mock(ProductWithQuantityDTO.class);
        UUID productId = UUID.randomUUID();
        when(productDTO.getItemId()).thenReturn(productId);
        when(productDTO.getQuantity()).thenReturn(2);

        Product product = mock(Product.class);
        when(product.getPledgeValue()).thenReturn(10.0);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Pledge pledge = pledgeFactory.create(new ProductWithQuantityDTO[]{productDTO});

        assertNotNull(pledge);
        assertEquals(20.0, pledge.getValue());
    }

    @Test
    void create_withInvalidProductId_throwsResponseStatusException() {
        ProductWithQuantityDTO productDTO = mock(ProductWithQuantityDTO.class);
        UUID productId = UUID.randomUUID();
        when(productDTO.getItemId()).thenReturn(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            pledgeFactory.create(new ProductWithQuantityDTO[]{productDTO});
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Product not found with id: " + productId, exception.getReason());
    }

    @Test
    void create_withZeroValueProducts_throwsInvalidPledgeException() {
        ProductWithQuantityDTO productDTO = mock(ProductWithQuantityDTO.class);
        UUID productId = UUID.randomUUID();
        when(productDTO.getItemId()).thenReturn(productId);
        when(productDTO.getQuantity()).thenReturn(2);

        Product product = mock(Product.class);
        when(product.getPledgeValue()).thenReturn(0.0);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        PledgeFactory.InvalidPledgeException exception = assertThrows(PledgeFactory.InvalidPledgeException.class, () -> {
            pledgeFactory.create(new ProductWithQuantityDTO[]{productDTO});
        });

        assertEquals("Invalid pledge value : 0.0 <= 0", exception.getMessage());
    }

    @Test
    void create_withNegativeValueProducts_throwsInvalidPledgeException() {
        ProductWithQuantityDTO productDTO = mock(ProductWithQuantityDTO.class);
        UUID productId = UUID.randomUUID();
        when(productDTO.getItemId()).thenReturn(productId);
        when(productDTO.getQuantity()).thenReturn(2);

        Product product = mock(Product.class);
        when(product.getPledgeValue()).thenReturn(-5.0);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        PledgeFactory.InvalidPledgeException exception = assertThrows(PledgeFactory.InvalidPledgeException.class, () -> {
            pledgeFactory.create(new ProductWithQuantityDTO[]{productDTO});
        });

        assertEquals("Invalid pledge value : -10.0 <= 0", exception.getMessage());
    }
}