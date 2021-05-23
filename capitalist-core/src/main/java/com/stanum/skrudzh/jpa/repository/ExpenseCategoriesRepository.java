package com.stanum.skrudzh.jpa.repository;

import com.stanum.skrudzh.jpa.model.*;
import com.stanum.skrudzh.jpa.model.base.Hashable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ExpenseCategoriesRepository extends JpaRepository<ExpenseCategoryEntity, Long> {

    @Query("from ExpenseCategoryEntity where basket = :basketEntity and monthlyPlannedCents is not null " +
            "and deletedAt is null and isVirtual = false ")
    Set<ExpenseCategoryEntity> getExpenseCategoriesWithMonthlyPlannedCents(BasketEntity basketEntity);

    @Query("select expctgr from ExpenseCategoryEntity expctgr left outer join CreditEntity crdt " +
            "on expctgr.creditEntity = crdt " +
            "where expctgr.basket.id = :basketId and expctgr.isBorrow = :isBorrow " +
            "and (crdt is null or crdt.isPaid = false) " +
            "and expctgr.deletedAt is null and expctgr.isVirtual = false " +
            "order by expctgr.rowOrder")
    Set<ExpenseCategoryEntity> getExpenseCategoryEntitiesByBasketAndBorrow(
            @Param("basketId") Long basketId,
            @Param("isBorrow") Boolean isBorrow);

    @Query("select expctgr from ExpenseCategoryEntity expctgr left outer join CreditEntity crdt " +
            "on expctgr.creditEntity = crdt " +
            "where expctgr.basket.id = :basketId " +
            "and expctgr.deletedAt is null and expctgr.isVirtual = false " +
            "and (crdt is null or crdt.isPaid = false) " +
            "order by expctgr.rowOrder")
    Set<ExpenseCategoryEntity> getExpenseCategoryEntitiesByBasket(
            @Param("basketId") Long basketId);

    Set<ExpenseCategoryEntity> findAllByUser(UserEntity userEntity);

    @Query("select expctgr from ExpenseCategoryEntity expctgr left outer join CreditEntity crdt " +
            "on expctgr.creditEntity = crdt " +
            "where (expctgr.basket.user.id = :userId or (expctgr.user is not null and expctgr.user.id = :userId)) " +
            " and expctgr.isBorrow = :isBorrow " +
            "and expctgr.deletedAt is null and expctgr.isVirtual = false " +
            "and (crdt is null or crdt.isPaid = false) " +
            "order by expctgr.basket.id, expctgr.rowOrder")
    Set<ExpenseCategoryEntity> getExpenseCategoryEntitiesByUserAndBorrow(
            @Param("userId") Long userId,
            @Param("isBorrow") Boolean isBorrow);

    @Query("select expctgr from ExpenseCategoryEntity expctgr left outer join CreditEntity crdt " +
            "on expctgr.creditEntity = crdt " +
            "where (expctgr.basket.user.id = :userId or (expctgr.user is not null and expctgr.user.id = :userId)) " +
            "and expctgr.deletedAt is null and expctgr.isVirtual = false " +
            "and (crdt is null or crdt.isPaid = false) " +
            "order by expctgr.basket.id, expctgr.rowOrder")
    Set<ExpenseCategoryEntity> getExpenseCategoryEntitiesByUser(
            @Param("userId") Long userId);

    @Query("from ExpenseCategoryEntity where isVirtual = true " +
            "and basket = :basketEntity and currency = :currencyCode and deletedAt is null")
    Optional<ExpenseCategoryEntity> getVirtualExpenseCategory(BasketEntity basketEntity, String currencyCode);

    @Query("from ExpenseCategoryEntity where basket = :basketEntity and isBorrow = :isBorrow and currency = :currencyCode " +
            "and deletedAt is null and isVirtual = false " +
            "order by rowOrder")
    Optional<ExpenseCategoryEntity> getBorrowExpenseCategoryFilteredByParams(BasketEntity basketEntity, Boolean isBorrow, String currencyCode);

    @Query("select max(rowOrder) from ExpenseCategoryEntity where basket = :basketEntity and deletedAt is null")
    Integer getLastRowOrderNumber(BasketEntity basketEntity);

    @Query("from ExpenseCategoryEntity where basket = :basketEntity and deletedAt is null and isVirtual = false " +
            "order by rowOrder")
    List<Rankable> findAllByBasketAndDeletedAtIsNullOrderByRowOrder(BasketEntity basketEntity);

    @Query("from ExpenseCategoryEntity where creditEntity = :creditEntity and deletedAt is null and isVirtual = false")
    Optional<ExpenseCategoryEntity> findByCreditEntityAndDeletedAtIsNull(CreditEntity creditEntity);

    @Query("from ExpenseCategoryEntity where creditEntity = :creditEntity and isVirtual = false")
    Optional<ExpenseCategoryEntity> findByCreditEntityWithDeleted(CreditEntity creditEntity);

    Set<ExpenseCategoryEntity> findAllByBasket(BasketEntity basketEntity);

    @Query("select distinct currency from ExpenseCategoryEntity where basket.user = :userEntity")
    Set<String> findAllExpenseCategoriesCurrencies(UserEntity userEntity);

    @Query("from ExpenseCategoryEntity expenseCategory " +
            "left join BasketEntity basket on expenseCategory.basket = basket " +
            "left join CreditEntity credit on expenseCategory.creditEntity = credit " +
            "where basket.user = :userEntity " +
            "and (credit is null or (credit is not null and credit.isPaid = false and credit.deletedAt is null)) " +
            "and expenseCategory.monthlyPlannedCents is not null " +
            "and expenseCategory.deletedAt is null")
    Set<ExpenseCategoryEntity> findAllWithPlannedExpenses(UserEntity userEntity);

    @Query("from ExpenseCategoryEntity where basket.user = :userEntity and monthlyPlannedCents is null " +
            "and deletedAt is null")
    Set<ExpenseCategoryEntity> findAllWithoutPlannedExpenses(UserEntity userEntity);

    @Query("from ExpenseCategoryEntity where basket = :basketEntity and prototypeKey = :prototypeKey and deletedAt is null")
    Optional<Hashable> getByPrototypeKey(BasketEntity basketEntity, String prototypeKey);

    @Query("select prototypeKey from ExpenseCategoryEntity where basket = :basketEntity and deletedAt is null and prototypeKey is not null")
    Set<String> getAllUsedPrototypeKeys(BasketEntity basketEntity);
}
