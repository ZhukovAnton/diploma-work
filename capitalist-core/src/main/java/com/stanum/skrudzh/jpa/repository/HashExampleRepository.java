package com.stanum.skrudzh.jpa.repository;

import com.stanum.skrudzh.jpa.model.HashExampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface HashExampleRepository extends JpaRepository<HashExampleEntity, Long> {

    @Query(value = "select * from hash_examples where " +
            "(?1 is null or country  is null " +
            "    or (country is not null and ?1 is not null and lower(country) like lower(cast(concat('%', ?1, '%') as varchar))))",
            nativeQuery = true)
    Set<HashExampleEntity> findAllByCountry(String country);
}
