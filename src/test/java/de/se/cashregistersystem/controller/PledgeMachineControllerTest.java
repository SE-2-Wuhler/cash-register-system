package de.se.cashregistersystem.controller;

import de.se.cashregistersystem.dto.ProductWithQuantityDTO;
import de.se.cashregistersystem.entity.Product;
import de.se.cashregistersystem.repository.PledgeRepository;
import de.se.cashregistersystem.service.PledgeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PledgeMachineControllerTest {

    @Mock
    private PledgeRepository pledgeRepository;

    @Mock
    private PledgeService pledgeService;

    @InjectMocks
    private PledgeMachineController pledgeMachineController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPledge_withValidProducts_createsPledge() {
        ProductWithQuantityDTO[] products = {new ProductWithQuantityDTO()};
        UUID pledgeId = UUID.randomUUID();
        when(pledgeService.createPledge(products)).thenReturn(pledgeId);

        ResponseEntity<UUID> response = pledgeMachineController.createPledge(products);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(pledgeId, response.getBody());
    }

    @Test
    void createPledge_withEmptyProducts_throwsBadRequest() {
        ProductWithQuantityDTO[] products = {};

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> pledgeMachineController.createPledge(products));
        ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot create pledge with empty item list");
        assertEquals(responseStatusException.getMessage(), exception.getMessage());
    }

    @Test
    void getAll_withPledgeItems_returnsItems() {
        List<Product> products = List.of(new Product(), new Product());
        when(pledgeService.getAllPledgeItems()).thenReturn(products);

        ResponseEntity<List<Product>> response = pledgeMachineController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(products, response.getBody());
    }

    @Test
    void getAll_withNoPledgeItems_throwsNoContent() {
        when(pledgeService.getAllPledgeItems()).thenReturn(List.of());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> pledgeMachineController.getAll());
        ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.NO_CONTENT, "No pledge items available");
        assertEquals(responseStatusException.getMessage(), exception.getMessage());
    }
}