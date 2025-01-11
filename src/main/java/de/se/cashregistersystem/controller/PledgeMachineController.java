package de.se.cashregistersystem.controller;



import de.se.cashregistersystem.dto.ItemWithQuantityDTO;
import de.se.cashregistersystem.dto.PledgeDTO;
import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.repository.PledgeRepository;
import de.se.cashregistersystem.service.PledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/pledge")

public class PledgeMachineController {

    @Autowired
    private PledgeRepository pledgeRepository;
    @Autowired
    private PledgeService service;

    @PostMapping("/create")
    public ResponseEntity<String> createPledge(@RequestBody ItemWithQuantityDTO[] itemWithQuantityDTO){

        try {
            UUID id = service.createPledge(itemWithQuantityDTO);
            return new ResponseEntity<String>("Pledge "+  id.toString() +  " has been created.",HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<String>("Failed to create Pledge", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }




}
