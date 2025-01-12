package de.se.cashregistersystem.repository;

import de.se.cashregistersystem.entity.Pledge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PledgeRepository extends JpaRepository<Pledge, UUID> {
    Optional<Pledge> findPledgeByBarcodeId(String barcode_id);
}
