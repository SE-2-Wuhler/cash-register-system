package de.se.cashregistersystem.repository;

import de.se.cashregistersystem.entity.Brand;
import de.se.cashregistersystem.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BrandRepository extends JpaRepository<Brand, UUID> {
}
