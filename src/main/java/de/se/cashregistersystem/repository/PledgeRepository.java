package de.se.cashregistersystem.repository;

import de.se.cashregistersystem.entity.Pledge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PledgeRepository extends JpaRepository<Pledge, UUID> {

}
