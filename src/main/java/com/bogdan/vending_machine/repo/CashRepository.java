package com.bogdan.vending_machine.repo;

import com.bogdan.vending_machine.entity.Cash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface CashRepository extends JpaRepository<Cash, Long> {

    @Query("select c from Cash c where c.type = :type")
    List<Cash> getCash(@Param("type") String type);

    @Transactional
    @Modifying
    @Query("delete from Cash c where c.type = :type and c.quantity = :quantity")
    void removeCash(@Param("type") String type, @Param("quantity") long quantity);
}
