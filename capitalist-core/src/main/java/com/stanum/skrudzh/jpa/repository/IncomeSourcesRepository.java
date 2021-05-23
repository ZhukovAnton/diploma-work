package com.stanum.skrudzh.jpa.repository;


import com.stanum.skrudzh.jpa.model.*;
import com.stanum.skrudzh.jpa.model.base.Hashable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface IncomeSourcesRepository extends JpaRepository<IncomeSourceEntity, Long> {

    @Query("from IncomeSourceEntity where user = :userEntity " +
            "and isBorrow = :isBorrow and currency = :currencyCode " +
            "and deletedAt is null and isVirtual = false " +
            "order by rowOrder")
    Optional<IncomeSourceEntity> getFirstByParams(UserEntity userEntity, Boolean isBorrow, String currencyCode);

    @Query(value = "select distinct ins from IncomeSourceEntity ins left join ActiveEntity act on ins.active = act" +
            " where (ins.user = :user) " +
            "and ((act is null) or (act.isIncomePlanned = :isIncomePlanned)) " +
            "and ins.deletedAt is null and ins.isVirtual = false " +
            "order by ins.rowOrder")
    List<IncomeSourceEntity> getByUserAndIsPlanned(
            @Param("user") UserEntity userEntity,
            @Param("isIncomePlanned") boolean isIncomePlanned);

    @Query(value = "select distinct ins from IncomeSourceEntity ins left join ActiveEntity act on ins.active = act" +
            " where (ins.user = :user and ins.isBorrow = :isBorrow) and " +
            "((act is null) or (act.isIncomePlanned = :isIncomePlanned)) " +
            "and ins.deletedAt is null and ins.isVirtual = false " +
            "order by ins.rowOrder")
    List<IncomeSourceEntity> getAllByUserAndIsBorrowAndIsIncomePlanned(
            @Param("user") UserEntity userEntity,
            @Param("isBorrow") boolean isBorrow,
            @Param("isIncomePlanned") boolean isIncomePlanned);

    @Query("from IncomeSourceEntity where user = :userEntity and deletedAt is null")
    Set<IncomeSourceEntity> findAllByUser(UserEntity userEntity);

    @Query("select max(rowOrder) from IncomeSourceEntity where user = :userEntity and deletedAt is null")
    Integer getLastRowOrderNumber(UserEntity userEntity);

    List<Rankable> findAllByUserAndDeletedAtIsNullOrderByRowOrder(UserEntity userEntity);

    @Query("from IncomeSourceEntity where active = :activeEntity " +
            "and deletedAt is null and isVirtual = false")
    IncomeSourceEntity findByActive(ActiveEntity activeEntity);

    @Query("from IncomeSourceEntity where active.id = :activeId")
    IncomeSourceEntity findByActiveId(Long activeId);

    @Query("from IncomeSourceEntity where user = :userEntity and currency = :currencyCode " +
            "and deletedAt is null and isVirtual = true")
    Optional<IncomeSourceEntity> findVirtualByUserAndCurrency(UserEntity userEntity, String currencyCode);

    @Query("select distinct currency from IncomeSourceEntity where user = :userEntity")
    Set<String> findAllIncomeSourcesCurrencies(UserEntity userEntity);

    @Query("from IncomeSourceEntity where user = :userEntity and prototypeKey = :prototypeKey and deletedAt is null")
    Optional<Hashable> findByPrototypeKey(UserEntity userEntity, String prototypeKey);

    @Query("select prototypeKey from IncomeSourceEntity where user = :userEntity and prototypeKey is not null " +
            "and deletedAt is null")
    Set<String> getAllUsedPrototypeKeys(UserEntity userEntity);

}
