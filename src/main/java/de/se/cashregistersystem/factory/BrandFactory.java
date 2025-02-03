package de.se.cashregistersystem.factory;

import de.se.cashregistersystem.entity.Brand;
import org.springframework.stereotype.Service;

@Service
public class BrandFactory {

    Brand create(String name) {
        return new Brand(name, "default");
    }

    Brand create(String name, String address) {
        return new Brand(name, address);
    }

}
