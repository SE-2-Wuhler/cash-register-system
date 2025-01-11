package de.se.cashregistersystem.factory;

import de.se.cashregistersystem.dto.PledgeDTO;
import de.se.cashregistersystem.entity.Pledge;
import de.se.cashregistersystem.util.POSBarcode;
import org.springframework.stereotype.Service;

@Service
public class PledgeFactory {

    public Pledge create(String barcode_id, double value){
        return new Pledge(barcode_id,value);
    }

    /**
     * Creates a copy of an existing Pledge instance.
     *
     * @param original The original Pledge to copy.
     * @return A new Pledge instance with the same properties as the original.
     */

}