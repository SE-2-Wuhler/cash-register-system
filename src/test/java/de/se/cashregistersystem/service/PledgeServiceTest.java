package de.se.cashregistersystem.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import de.se.cashregistersystem.dto.ProductWithQuantityDTO;
import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.entity.Product;
import de.se.cashregistersystem.factory.PledgeFactory;
import de.se.cashregistersystem.repository.PledgeRepository;
import de.se.cashregistersystem.repository.ProductRepository;

public class PledgeServiceTest {

    @Mock
    private PledgeRepository pledgeRepository;

    @Mock
    private PrintingService printingService;

    @Mock
    private PledgeFactory pledgeFactory;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private PledgeMachineService pledgeMachineService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreatePledge_Success() throws PledgeFactory.InvalidPledgeException {
        // Arrange
        ProductWithQuantityDTO[] products = new ProductWithQuantityDTO[0];
        Pledge newPledge = mock(Pledge.class);
        String barcodeId = "barcode123";
        UUID pledgeId = UUID.randomUUID();

        when(pledgeFactory.create(products)).thenReturn(newPledge);
        when(printingService.printPledgeReceipt(newPledge)).thenReturn(barcodeId);
        when(pledgeRepository.save(newPledge)).thenReturn(newPledge);
        when(newPledge.getId()).thenReturn(pledgeId);

        // Act
        UUID result = pledgeMachineService.createPledge(products);

        // Assert
        assertEquals(pledgeId, result);
        verify(pledgeFactory).create(products);
        verify(printingService).printPledgeReceipt(newPledge);
        verify(newPledge).setBarcodeId(barcodeId);
        verify(pledgeRepository).save(newPledge);
    }

    @Test
    public void testCreatePledge_InvalidPledgeException() throws PledgeFactory.InvalidPledgeException {
        ProductWithQuantityDTO[] products = new ProductWithQuantityDTO[0];

        when(pledgeFactory.create(products)).thenThrow(new PledgeFactory.InvalidPledgeException("Invalid pledge"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            pledgeMachineService.createPledge(products);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Invalid pledge", exception.getReason());
        verify(pledgeFactory).create(products);
        verifyNoInteractions(printingService);
        verifyNoInteractions(pledgeRepository);
    }

    @Test
    public void testGetAllPledgeItems_Success() {
        List<Product> products = List.of(new Product());

        when(productRepository.findProductsWithPositivePledgeValue()).thenReturn(Optional.of(products));

        List<Product> result = pledgeMachineService.getAllPledgeItems();

        assertEquals(products, result);
        verify(productRepository).findProductsWithPositivePledgeValue();
    }

    @Test
    public void testGetAllPledgeItems_NotFound() {
        when(productRepository.findProductsWithPositivePledgeValue()).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            pledgeMachineService.getAllPledgeItems();
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No Items with positive Pledge Values found", exception.getReason());
        verify(productRepository).findProductsWithPositivePledgeValue();
    }
}