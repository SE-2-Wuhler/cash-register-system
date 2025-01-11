package de.se.cashregistersystem.factory;

import de.se.cashregistersystem.dto.PledgeDTO;
import de.se.cashregistersystem.entity.Pledge;
import org.springframework.stereotype.Service;

@Service
public class PledgeFactory {

    public Pledge create(PledgeDTO pledgeDTO){
        return new Pledge(pledgeDTO.getBarcodeId(),pledgeDTO.getValue());
    }

    /**
     * Creates a copy of an existing Pledge instance.
     *
     * @param original The original Pledge to copy.
     * @return A new Pledge instance with the same properties as the original.
     */

}