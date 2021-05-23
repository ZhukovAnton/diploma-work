package com.stanum.skrudzh.jpa.repository;

import com.stanum.skrudzh.jpa.model.CreditTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface CreditTypeRepository extends JpaRepository<CreditTypeEntity, Long> {

    @Query("from CreditTypeEntity order by rowOrder")
    Set<CreditTypeEntity> findAllOrderByRowOrder();
}
