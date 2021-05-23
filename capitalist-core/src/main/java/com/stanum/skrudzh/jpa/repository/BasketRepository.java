package com.stanum.skrudzh.jpa.repository;

import com.stanum.skrudzh.jpa.model.BasketEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.enums.BasketTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface BasketRepository extends JpaRepository<BasketEntity, Long> {
    @Query("from BasketEntity where user.id = :userId")
    Set<BasketEntity> findBasketEntitiesByUser(@Param("userId") Long userId);

    Optional<BasketEntity> findBasketEntityByUserAndBasketType(UserEntity userEntity, BasketTypeEnum basketTypeEnum);

    @Query("select (bskt.user.id = :userId) from BasketEntity bskt where bskt.id = :basketId")
    Boolean checkUserId(Long basketId, Long userId);
}
