package com.stanum.skrudzh.jpa.repository;

import com.stanum.skrudzh.jpa.model.BorrowEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.enums.BorrowTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface BorrowRepository extends JpaRepository<BorrowEntity, Long> {
    @Query("from BorrowEntity where user = :userEntity " +
            "and type = :borrowType and not (isReturned = true) " +
            "and deletedAt is null " +
            "order by borrowedAt desc ")
    Set<BorrowEntity> getNotReturnedUserBorrows(UserEntity userEntity, BorrowTypeEnum borrowType);

    @Query("from BorrowEntity where user = :userEntity " +
            "and type = :borrowType and not (isReturned = true) " +
            "and amountCurrency = :currencyCode " +
            "and deletedAt is null " +
            "order by borrowedAt desc ")
    Set<BorrowEntity> getNotReturnedBorrowsByCurrency(UserEntity userEntity, BorrowTypeEnum borrowType, String currencyCode);

    @Query("from BorrowEntity where id = :id and type = :type")
    Optional<BorrowEntity> findByIdAndType(Long id, BorrowTypeEnum type);

    Set<BorrowEntity> findAllByUser(UserEntity user);

    Set<BorrowEntity> findAllByUserAndType(UserEntity userEntity, BorrowTypeEnum borrowType);

    @Query("select distinct amountCurrency from BorrowEntity where user = :userEntity")
    Set<String> findAllBorrowsCurrencies(UserEntity userEntity);

}
