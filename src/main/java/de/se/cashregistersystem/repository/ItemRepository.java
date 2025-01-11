package de.se.cashregistersystem.repository;

import de.se.cashregistersystem.entity.Item;
import de.se.cashregistersystem.entity.ItemTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {


}
