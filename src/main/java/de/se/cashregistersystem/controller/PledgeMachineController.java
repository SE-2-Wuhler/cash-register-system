package de.se.cashregistersystem.controller;


import de.se.cashregistersystem.dto.PledgeDTO;
import de.se.cashregistersystem.dto.PledgeItemDTO;
import de.se.cashregistersystem.entity.Item;
import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.repository.PledgeRepository;
import de.se.cashregistersystem.service.PledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/pledge")

public class PledgeMachineController {

    @Autowired
    private PledgeRepository pledgeRepository;
    @Autowired
    private PledgeService service;

    @PostMapping("/create")
    public ResponseEntity<String> createPledge(@RequestBody PledgeDTO pledgeDTO){

        try {
            service.createPledge(pledgeDTO);
        } catch (Exception e) {
            return new ResponseEntity<String>("Failed to create Pledge", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>("Pledge has been created.",HttpStatus.CREATED);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll(){
        try {
            return new ResponseEntity<List<Item>>(service.getAllPledgeItems(), HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<String>("Failed to load Pledge Items", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }





}
