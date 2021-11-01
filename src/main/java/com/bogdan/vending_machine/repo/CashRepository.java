package com.bogdan.vending_machine.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bogdan.vending_machine.entity.Cash;

import javax.transaction.Transactional;

@Repository
public interface CashRepository extends JpaRepository<Cash, Long> {

	@Query("select c from Cash c where c.type = :type")
	List<Cash> getCash(@Param("type") int type);

	@Transactional
	@Modifying
	@Query("delete from Cash c where  c.quantity = :quantity")
	void removeCash(@Param("quantity") double quantity);
}
