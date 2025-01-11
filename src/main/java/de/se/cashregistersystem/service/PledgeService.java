package de.se.cashregistersystem.service;

import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.repository.PledgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PledgeService {

    @Autowired
    private PledgeRepository pledgeRepository;

    public Pledge createPledge(Pledge pledge) {

        //TODO: Print bon

        try {
            return pledgeRepository.save(pledge);
        } catch (Exception e) {
            throw new RuntimeException("Insert of pledge failed "+ e);
        }


    }
}
