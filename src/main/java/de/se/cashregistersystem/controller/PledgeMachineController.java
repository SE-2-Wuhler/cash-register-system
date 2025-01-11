package de.se.cashregistersystem.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class PledgeMachineController {
    @PostMapping("/pledge/create")
    public ResponseEntity<String> createPledge(){




        return new ResponseEntity<String>("Pledge created",HttpStatus.CREATED);
    }




}
