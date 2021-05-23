package com.stanum.skrudzh.jpa.repository;

import com.stanum.skrudzh.jpa.model.ActiveEntity;
import com.stanum.skrudzh.jpa.model.BasketEntity;
import com.stanum.skrudzh.jpa.model.Rankable;
import com.stanum.skrudzh.jpa.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface ActiveRepository extends JpaRepository<ActiveEntity, Long> {

    @Query("from ActiveEntity where basketEntity.id = :basketId and deletedAt is null " +
            "order by rowOrder")
    Set<ActiveEntity> getAllByBasket(Long basketId);

    @Query("from ActiveEntity active left join BasketEntity basket on active.basketEntity = basket where (active.user = :userEntity " +
            " or basket.user = :userEntity) and active.deletedAt is null " +
            "order by active.rowOrder")
    Set<ActiveEntity> getAllByUser(UserEntity userEntity);

    @Query("from ActiveEntity where basketEntity = :basketEntity " +
            "and monthlyPaymentCents is not null and deletedAt is null")
    Set<ActiveEntity> getActivesWithMonthlyPlannedPayments(BasketEntity basketEntity);

    @Query("from ActiveEntity a left join BasketEntity b on a.basketEntity.id = b.id where (a.user = :userEntity " +
            " or b.user = :userEntity) and a.monthlyPaymentCents is not null and a.deletedAt is null ")
    Set<ActiveEntity> getActivesWithMonthlyPlannedPayments(UserEntity userEntity);

    @Query("select max(rowOrder) from ActiveEntity where basketEntity = :basketEntity and deletedAt is null")
    Integer getLastRowOrderNumber(BasketEntity basketEntity);

    Integer countActiveEntitiesByBasketEntityAndDeletedAtIsNull(BasketEntity basketEntity);

    @Query("from ActiveEntity where basketEntity = :basketEntity and deletedAt is null order by rowOrder")
    List<Rankable> findAllRankable(BasketEntity basketEntity);

    @Query("from ActiveEntity active left join BasketEntity basket " +
            "on active.basketEntity = basket where basket.user = :userEntity or active.user = :userEntity")
    Set<ActiveEntity> findAllByUserEntity(UserEntity userEntity);

    @Query("select distinct active.currency from ActiveEntity active left join BasketEntity basket on " +
            "active.basketEntity = basket where active.user = :userEntity or basket.user = :userEntity")
    Set<String> findAllActivesCurrencies(UserEntity userEntity);
}
