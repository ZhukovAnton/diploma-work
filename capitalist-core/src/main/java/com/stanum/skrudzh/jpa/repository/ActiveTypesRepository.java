package com.stanum.skrudzh.jpa.repository;

import com.stanum.skrudzh.jpa.model.ActiveTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ActiveTypesRepository extends JpaRepository<ActiveTypeEntity, Long> {
    @Query("from ActiveTypeEntity order by rowOrder")
    List<ActiveTypeEntity> findAllOrdered();
}
