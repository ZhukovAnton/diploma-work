package com.stanum.skrudzh.jpa.repository;

import com.stanum.skrudzh.jpa.model.AccountConnectionEntity;
import com.stanum.skrudzh.jpa.model.ExpenseSourceEntity;
import com.stanum.skrudzh.jpa.model.Rankable;
import com.stanum.skrudzh.jpa.model.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ExpenseSourcesRepository extends JpaRepository<ExpenseSourceEntity, Long> {
    Set<ExpenseSourceEntity> getAllByUserAndIsVirtualAndCurrencyAndDeletedAtIsNullOrderByRowOrder(UserEntity user, Boolean isVirtual, String currency);

    Set<ExpenseSourceEntity> getAllByUserAndIsVirtualAndDeletedAtIsNullOrderByRowOrder(UserEntity user, Boolean isVisual);

    Set<ExpenseSourceEntity> getAllByAccountConnectionEntity(AccountConnectionEntity accountConnectionEntity);

    @Query("from ExpenseSourceEntity where user = :user " +
            "and isVirtual = :isVirtual and currency = :currencyCode and deletedAt is null " +
            "order by rowOrder")
    List<ExpenseSourceEntity> getFirstByParams(UserEntity user, Boolean isVirtual, String currencyCode, Pageable first);

    @Query("select max(rowOrder) from ExpenseSourceEntity where user = :user " +
            "and deletedAt is null")
    Integer getLastRowOrderNumber(UserEntity user);

    @Query("from ExpenseSourceEntity where id = :id")
    Optional<ExpenseSourceEntity> findByIdWithDeleted(Long id);

    @Query("from ExpenseSourceEntity where deletedAt is null and user = :user " +
            "and isVirtual = false order by rowOrder")
    List<Rankable> findAllRankableByUser(UserEntity user);

    Set<ExpenseSourceEntity> findAllByUser(UserEntity user);

    @Query("select distinct currency from ExpenseSourceEntity where user = :user")
    Set<String> findAllExpenseSourcesCurrencies(UserEntity user);

    @Query("from ExpenseSourceEntity where isVirtual = false and accountConnectionEntity is null and deletedAt is null")
    Set<ExpenseSourceEntity> findAllActual();

    @Query("select prototypeKey from ExpenseSourceEntity where user = :user and prototypeKey is not null " +
            "and deletedAt is null")
    Set<String> getAllUsedPrototypeKeys(UserEntity user);
}
