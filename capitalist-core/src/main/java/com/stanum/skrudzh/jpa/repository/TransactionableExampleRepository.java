package com.stanum.skrudzh.jpa.repository;

import com.stanum.skrudzh.jpa.model.TransactionableExampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TransactionableExampleRepository extends JpaRepository<TransactionableExampleEntity, Long> {

    @Query(value = "select * from transactionable_examples where " +
            "((?1 is null and country is null) "  +
            "or (?1 is not null and (country is null or lower(country) like concat('%', lower(cast(?1 as varchar)), '%')))) " +
            "order by row_order", nativeQuery = true)
    List<TransactionableExampleEntity> findAllByCountry(String country);

    @Query(value = "select * from transactionable_examples where " +
            "transactionable_type = ?1 and create_by_default = ?2 " +
            "and (?3 is null or country is null " +
            "    or (country is not null and ?3 is not null and lower(country) like lower(cast(concat('%', ?3, '%') as varchar)))) " +
            "order by row_order", nativeQuery = true)
    List<TransactionableExampleEntity> findAllByTypeCountryAndCreateByDefault(String transactionableType, Boolean createByDefault, String region);

    @Query("from TransactionableExampleEntity where prototypeKey = :prototypeKey " +
            "and ((:country is null and country is null) " +
            "or (:country is not null and (country is null or lower(country) like concat('%', lower(:country), '%'))))")
    Optional<TransactionableExampleEntity> findByPrototypeKeyAndCountry(String prototypeKey, String country);

    Optional<TransactionableExampleEntity> findByName(String name);
}
