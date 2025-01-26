package de.se.cashregistersystem.repository;

import de.se.cashregistersystem.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BrandRepository extends JpaRepository<Brand, UUID> {

    Optional<Brand> findBrandByName(String name);

}
