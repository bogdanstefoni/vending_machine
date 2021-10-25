package com.bogdan.vending_machine.repo;

import com.bogdan.vending_machine.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("Select i from Item i where i.itemName = :itemName")
    List<Item> getItemByName(@Param("itemName") String itemName);

}
