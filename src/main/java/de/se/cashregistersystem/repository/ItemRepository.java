package de.se.cashregistersystem.repository;

import de.se.cashregistersystem.entity.Item;
import de.se.cashregistersystem.entity.ItemTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {
    @Query("SELECT i FROM Item i WHERE i.pledgeValue > 0")
    List<Item> findItemsWithPositivePledgeValue();
}
