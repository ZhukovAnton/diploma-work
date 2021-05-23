package com.stanum.skrudzh.jpa.repository;

import com.stanum.skrudzh.jpa.model.CreditEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface CreditRepository extends JpaRepository<CreditEntity, Long> {

    @Query("from CreditEntity where user = :userEntity and isPaid = false and deletedAt is null")
    Set<CreditEntity> getUnpaidCredits(UserEntity userEntity);

    @Query("from CreditEntity where id = :creditId ")
    CreditEntity findCreditByIdWithNull(Long creditId);

    @Query("from CreditEntity where user = :userEntity and deletedAt is null")
    Set<CreditEntity> findAllByUserEntity(UserEntity userEntity);

    @Query("select distinct currency from CreditEntity where user = :userEntity")
    Set<String> findAllCreditsCurrencies(UserEntity userEntity);
}
