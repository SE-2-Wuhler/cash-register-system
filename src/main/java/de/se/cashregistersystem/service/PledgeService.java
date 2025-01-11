package de.se.cashregistersystem.service;

import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.repository.PledgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PledgeService {

    @Autowired
    private PledgeRepository pledgeRepository;

    public Pledge createPledge( String barcodeId,double value) {
        Pledge pledge = new Pledge();
        pledge.setBarcodeId(barcodeId);
        pledge.setValue(value);
        return pledgeRepository.save(pledge);

    }
}
