package de.se.cashregistersystem.controller;


import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.repository.PledgeRepository;
import de.se.cashregistersystem.service.PledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class PledgeMachineController {

    @Autowired
    private PledgeRepository pledgeRepository;
    @Autowired
    private PledgeService service;

    @PostMapping("/pledge/create")
    public ResponseEntity<String> createPledge(@RequestBody Pledge pledge){

        try {
            service.createPledge(pledge);
        } catch (Exception e) {
            return new ResponseEntity<String>("Failed to create Pledge", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>("Pledge has been created.",HttpStatus.CREATED);
    }




}
